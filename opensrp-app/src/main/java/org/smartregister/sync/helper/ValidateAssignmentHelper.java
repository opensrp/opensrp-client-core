package org.smartregister.sync.helper;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Response;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.util.SyncUtils;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

import static org.smartregister.account.AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED;
import static org.smartregister.sync.helper.LocationServiceHelper.LOCATION_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.LocationServiceHelper.STRUCTURES_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.PlanIntentServiceHelper.PLAN_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.TaskServiceHelper.TASK_LAST_SYNC_DATE;

/**
 * Created by samuelgithengi on 9/16/20.
 */
public class ValidateAssignmentHelper extends BaseHelper {

    private static final String USER_ASSIGNMENT_URL = "/rest/organization/user-assignment";

    public static final String ACTION_ASSIGNMENT_REMOVED = "action_assignment_removed";

    public static final String ASSIGNMENTS_REMOVED = "assignments_removed";

    public static final Gson gson = new Gson();

    private SyncUtils syncUtils;

    private UserService userService;

    private PlanDefinitionRepository planDefinitionRepository;

    private LocationRepository locationRepository;

    private AllSettings settingsRepository;

    private AllSharedPreferences allSharedPreferences;

    public ValidateAssignmentHelper(SyncUtils syncUtils) {
        this.syncUtils = syncUtils;
        userService = CoreLibrary.getInstance().context().userService();
        planDefinitionRepository = CoreLibrary.getInstance().context().getPlanDefinitionRepository();
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
        settingsRepository = CoreLibrary.getInstance().context().allSettings();
        allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    }

    public void validateUserAssignment() {
        boolean keycloakConfigured = CoreLibrary.getInstance().context().allSharedPreferences().getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        if (!keycloakConfigured) {
            return;
        }
        try {
            String assignment = getUserAssignment();
            if (StringUtils.isNotBlank(assignment)) {
                UserAssignmentDTO currentUserAssignment = new Gson().fromJson(assignment, UserAssignmentDTO.class);
                Set<Long> existingOrganizations = userService.fetchOrganizations();
                Set<String> existingJurisdictions = new HashSet<>(locationRepository.getAllLocationIds());
                Set<String> existingPlans = planDefinitionRepository.findAllPlanDefinitionIds();
                boolean newAssignments = hasNewAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions);
                UserAssignmentDTO removedAssignments = getRemovedAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions, existingPlans);
                processRemovedAssignments(removedAssignments);
                if (newAssignments) {
                    logoff(R.string.account_new_assignment_logged_off);
                    resetSync();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_ASSIGNMENT_REMOVED);
                    intent.putExtra(ASSIGNMENTS_REMOVED, removedAssignments);
                    CoreLibrary.getInstance().context().applicationContext().sendBroadcast(intent);
                }
            }
        } catch (NoHttpResponseException e) {
            Timber.e(e);
        }
    }

    private void resetSync() {
        allSharedPreferences.savePreference(LOCATION_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(STRUCTURES_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(STRUCTURES_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(PLAN_LAST_SYNC_DATE, "0");
        allSharedPreferences.savePreference(TASK_LAST_SYNC_DATE, "0");
        allSharedPreferences.saveLastSyncDate(0);
    }

    private void logoff(@StringRes int message) {
        try {
            syncUtils.logoutUser(message);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void processRemovedAssignments(UserAssignmentDTO removedAssignments) {
        if (!Utils.isEmptyCollection(removedAssignments.getPlans())) {
            planDefinitionRepository.deletePlans(removedAssignments.getPlans());
        }

        if (!Utils.isEmptyCollection(removedAssignments.getOrganizationIds())) {
            Set<Long> ids = userService.fetchOrganizations();
            ids.removeAll(removedAssignments.getOrganizationIds());
            userService.saveOrganizations(new ArrayList<>(ids));
        }
        if (!Utils.isEmptyCollection(removedAssignments.getJurisdictions())) {
            locationRepository.deleteLocations(removedAssignments.getJurisdictions());
            removeLocationsFromHierarchy(removedAssignments.getJurisdictions());
            Set<String> prefsIds = userService.fetchJurisdictionIds();
            prefsIds.removeAll(removedAssignments.getJurisdictions());
            userService.saveJurisdictionIds(prefsIds);
        }

    }

    @VisibleForTesting
    protected void removeLocationsFromHierarchy(Set<String> removedAssignments) {
        LocationTree locationTree = gson.fromJson(settingsRepository.fetchANMLocation(), LocationTree.class);
        for (String removedAssignment : removedAssignments) {
            locationTree.deleteLocation(removedAssignment);
        }
        settingsRepository.saveANMLocation(gson.toJson(locationTree));
        CoreLibrary.getInstance().context().anmLocationController().evict();
        String defaultLocationUuid = allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM());
        if (StringUtils.isNotBlank(defaultLocationUuid) && removedAssignments.contains(defaultLocationUuid)) {
            logoff(R.string.account_new_assignment_logged_off);
        }
    }

    private UserAssignmentDTO getRemovedAssignments(UserAssignmentDTO currentUserAssignment, Set<Long> existingOrganizations, Set<String> existingJurisdictions, Set<String> existingPlans) {
        existingJurisdictions.removeAll(currentUserAssignment.getJurisdictions());
        existingOrganizations.removeAll(currentUserAssignment.getOrganizationIds());
        existingPlans.removeAll(currentUserAssignment.getPlans());
        return UserAssignmentDTO.builder().jurisdictions(existingJurisdictions).organizationIds(existingOrganizations).plans(existingPlans).build();

    }

    private boolean hasNewAssignments(UserAssignmentDTO currentUserAssignment, Set<Long> existingOrganizations, Set<String> existingJurisdictions) {
        return !existingOrganizations.containsAll(currentUserAssignment.getOrganizationIds()) || !existingJurisdictions.containsAll(currentUserAssignment.getJurisdictions());
    }

    private String getUserAssignment() throws NoHttpResponseException {

        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(USER_ASSIGNMENT_URL + " http agent is null");
        }

        String baseUrl = getFormattedBaseUrl();


        Response resp = httpAgent.fetch(MessageFormat.format("{0}{1}", baseUrl, USER_ASSIGNMENT_URL));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(USER_ASSIGNMENT_URL + " not returned data");
        }

        return resp.payload().toString();
    }
}