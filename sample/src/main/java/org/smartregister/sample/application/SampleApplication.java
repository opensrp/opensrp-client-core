package org.smartregister.sample.application;

import android.app.Activity;

import androidx.annotation.NonNull;

import org.smartregister.AllConstants;
import org.smartregister.BuildConfig;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.domain.jsonmapping.User;
import org.smartregister.domain.jsonmapping.util.Team;
import org.smartregister.domain.jsonmapping.util.TeamLocation;
import org.smartregister.domain.jsonmapping.util.TeamMember;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.sample.SampleAppFormActivity;
import org.smartregister.sample.configuration.FormProcessor;
import org.smartregister.sample.configuration.RegisterQueryProvider;
import org.smartregister.sample.repository.SampleRepository;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.activity.FormActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by keyman on 14/08/2017.
 */
public class SampleApplication extends DrishtiApplication {

    private static CommonFtsObject commonFtsObject;

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }


    private static String[] getFtsTables() {
        //return new String[]{Constants.Table.CHILD, Constants.Table.MOTHER, OpdDbConstants.KEY.TABLE};
        return new String[]{"ec_client"};
    }

    private static String[] getFtsSearchFields(String tableName) {
        /*if (tableName.equals(Constants.Table.CHILD)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(Constants.Table.MOTHER)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.MIDDLE_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        } else if (tableName.equals(OpdDbConstants.KEY.TABLE)) {
            return new String[]{Constants.Columns.FIRST_NAME, Constants.Columns.LAST_NAME, Constants.Columns.DOB, Constants.Columns.LAST_INTERACTED_WITH};
        }

        return null;*/
        return new String[]{"first_name", "last_name", "middle_name", "dob", "last_interacted_with", "date_removed"};
    }

    private static String[] getFtsSortFields(String tableName) {
        /*if (tableName.equals(Constants.Table.CHILD)) {
            List<String> names = new ArrayList<>();
            names.add(Constants.Columns.FIRST_NAME);
            names.add(Constants.Columns.MIDDLE_NAME);
            names.add(Constants.Columns.LAST_NAME);
            names.add(Constants.Columns.DOB);

            return names.toArray(new String[names.size()]);
        } else if (tableName.equals(OpdDbConstants.KEY.TABLE)) {

            return new String[]{OpdDbConstants.KEY.BASE_ENTITY_ID, OpdDbConstants.KEY.FIRST_NAME, OpdDbConstants.KEY.LAST_NAME,
                    OpdDbConstants.KEY.LAST_INTERACTED_WITH, OpdDbConstants.KEY.DATE_REMOVED};
        }*/
        return new String[]{"zeir_id", "last_interacted_with", "date_removed"};
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context, new SampleSyncConfiguration(), BuildConfig.BUILD_TIMESTAMP);
        SyncStatusBroadcastReceiver.init(this);

        createCustomLibrary();
        LocationTagsConfiguration locationTagsConfiguration = new LocationTagsConfiguration();
        LocationHelper.init(locationTagsConfiguration.getAllowedLevels(), locationTagsConfiguration.getDefaultLocationLevel());

        addLoggedInState();

        getRepository();
    }

    private void addLoggedInState() {
        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword("dsd".getBytes());
        context.session().setPassword("dsd".getBytes());

        context.allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, CoreLibrary.getInstance().context().getAppProperties().getProperty(AllConstants.DRISHTI_BASE_URL));

        // Save credentials
        LoginResponseData loginResponseData = new LoginResponseData();
        loginResponseData.user = new User("base-entity-id", "demo", "dsd".toCharArray(), "my-salt");
        loginResponseData.team = new TeamMember();
        loginResponseData.team.team = new Team();
        loginResponseData.team.team.uuid = "d4560330-7868-4556-afd8-57e51946cee5";
        Set<TeamLocation> teamLocationSet = new HashSet<>();
        TeamLocation teamLocation = new TeamLocation();
        teamLocation.name = "Ayan";
        teamLocation.display = "Ayan";
        teamLocation.uuid = "a1550593-a204-4184-9e4b-2a1f72a915d1";
        teamLocationSet.add(teamLocation);

        loginResponseData.team.locations = teamLocationSet;

        context.userService().saveUserCredentials("demo", "dsd".toCharArray(), loginResponseData);

        // Add unique ids
        sampleUniqueIds();
    }

    private void sampleUniqueIds() {
        List<String> ids = generateIds(20);
        CoreLibrary.getInstance().context().getUniqueIdRepository().bulkInsertOpenmrsIds(ids);
    }


    private List<String> generateIds(int size) {
        List<String> ids = new ArrayList<>();
        Random r = new Random();

        for (int i = 10; i < size; i++) {
            Integer randomInt = r.nextInt(10000) + 1;
            ids.add(formatSampleId(randomInt.toString()));
        }

        return ids;
    }

    private String formatSampleId(String openmrsId) {
        int lastIndex = openmrsId.length() - 1;
        String tail = openmrsId.substring(lastIndex);
        return openmrsId.substring(0, lastIndex) + "-" + tail;
    }

    @Override
    public String getUsername() {
        return "demo";
    }

    private void createCustomLibrary() {
        ModuleConfiguration customLibraryConfiguration = new ModuleConfiguration.Builder("ONA Library", RegisterQueryProvider.class, new ConfigViewsLib(), ActivityStarter.class)
                .setModuleMetadata(new ModuleMetadata("opd_registration"
                        , "ec_client"
                        , "Opd Registration"
                        , "UPDATE OPD REGISTRATION",
                        new LocationTagsConfiguration(),
                        "custom-family",
                        FormActivity.class, BaseProfileActivity.class, false, ""
                        ))
                .setModuleFormProcessorClass(FormProcessor.class)
                .setJsonFormActivity(SampleAppFormActivity.class)
                .build();

        CoreLibrary.getInstance()
                .addModuleConfiguration(true, "custom-family", customLibraryConfiguration);
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
    }


    @Override
    public void logoutCurrentUser() {

    }

    static class ConfigViewsLib implements ModuleConfiguration.ConfigurableViewsLibrary {

        @Override
        public void registerViewConfigurations(List<String> viewIdentifiers) {

        }

        @Override
        public void unregisterViewConfigurations(List<String> viewIdentifiers) {

        }
    }

    static class ActivityStarter implements org.smartregister.configuration.ActivityStarter {

        @Override
        public void startProfileActivity(@NonNull Activity contextActivity, @NonNull CommonPersonObjectClient commonPersonObjectClient) {

        }
    }

    static class LocationTagsConfiguration implements org.smartregister.configuration.LocationTagsConfiguration {

        @NonNull
        @Override
        public ArrayList<String> getAllowedLevels() {
            return new ArrayList<String>(Arrays.asList("Country","County", "Town","Region","District","Ward" , "Health Facility", "Village", "Village Sublocations"));
        }

        @NonNull
        @Override
        public String getDefaultLocationLevel() {
            return "Village Sublocations";
        }

        @NonNull
        @Override
        public ArrayList<String> getLocationLevels() {
            return new ArrayList<>(Arrays.asList("Country","County", "Town","Region","District","Ward" , "Health Facility", "Village", "Village Sublocations"));
        }

        @NonNull
        @Override
        public ArrayList<String> getHealthFacilityLevels() {
            return new ArrayList<String>(Arrays.asList("County", "Town", "MOH Jhpiego Facility Name", "Village"));
        }
    }
    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new SampleRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Timber.e(e);
        }
        return repository;
    }



}
