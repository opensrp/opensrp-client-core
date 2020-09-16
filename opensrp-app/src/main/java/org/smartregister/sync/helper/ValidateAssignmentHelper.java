package org.smartregister.sync.helper;

import android.content.Intent;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Location;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Response;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.util.SyncUtils;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by samuelgithengi on 9/16/20.
 */
public class ValidateAssignmentHelper extends BaseHelper {

    private static final String USER_ASSIGNMENT_URL = "/rest/organization/user-assignment";

    public static final String ACTION_ASSIGNMENT_REMOVED = "action_assignment_removed";

    private SyncUtils syncUtils;

    private UserService userService;

    private PlanDefinitionRepository planDefinitionRepository;

    private LocationRepository locationRepository;

    public ValidateAssignmentHelper(SyncUtils syncUtils) {
        this.syncUtils = syncUtils;
        userService = CoreLibrary.getInstance().context().userService();
        planDefinitionRepository = CoreLibrary.getInstance().context().getPlanDefinitionRepository();
        locationRepository = CoreLibrary.getInstance().context().getLocationRepository();
    }

    public void validateUserAssignment() {
        try {
            String assignment = getUserAssignment();
            if (StringUtils.isNotBlank(assignment)) {
                UserAssignmentDTO currentUserAssignment = new Gson().fromJson(assignment, UserAssignmentDTO.class);
                Set<Long> existingOrganizations = userService.fetchOrganizations();
                Set<String> existingJurisdictions = userService.fetchJurisdictionIds();
                Set<String> existingPlans = planDefinitionRepository.findAllPlanDefinitionIds();
                boolean newAssignments = hasNewAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions);
                UserAssignmentDTO removedAssignments = getRemovedAssignments(currentUserAssignment, existingOrganizations, existingJurisdictions, existingPlans);
                processRemovedAssignments(removedAssignments);
                if (newAssignments) {
                    processNewAssignments();
                }
            }
        } catch (NoHttpResponseException e) {
            Timber.e(e);
        }
    }

    private void processNewAssignments() {
        try {
            syncUtils.logoutUser(R.string.account_assignment_changed_logged_off);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void processRemovedAssignments(UserAssignmentDTO removedAssignments) {
        if (!Utils.isEmptyCollection(removedAssignments.getPlans())) {
            for (PlanDefinition plan : planDefinitionRepository.findPlanDefinitionByIds(removedAssignments.getPlans())) {
                plan.setStatus(PlanDefinition.PlanStatus.RETIRED);
                planDefinitionRepository.addOrUpdate(plan);
            }
        }

        if (!Utils.isEmptyCollection(removedAssignments.getOrganizationIds())) {
            Set<Long> ids = userService.fetchOrganizations();
            ids.removeAll(removedAssignments.getOrganizationIds());
            userService.saveOrganizations(new ArrayList<>(ids));
        }
        if (!Utils.isEmptyCollection(removedAssignments.getJurisdictions())) {
            for (Location location : locationRepository.getLocationsByIds(new ArrayList<>(removedAssignments.getJurisdictions()))) {
                location.getProperties().setStatus(LocationProperty.PropertyStatus.INACTIVE);
                locationRepository.addOrUpdate(location);
            }
        }

        Intent intent = new Intent();
        intent.setAction(ACTION_ASSIGNMENT_REMOVED);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, removedAssignments);

        CoreLibrary.getInstance().context().applicationContext().sendBroadcast(intent);
    }

    private UserAssignmentDTO getRemovedAssignments(UserAssignmentDTO currentUserAssignment, Set<Long> existingOrganizations, Set<String> existingJurisdictions, Set<String> existingPlans) {
        existingJurisdictions.removeAll(currentUserAssignment.getJurisdictions());
        existingOrganizations.removeAll(currentUserAssignment.getOrganizationIds());
        existingOrganizations.removeAll(currentUserAssignment.getOrganizationIds());
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
