package org.smartregister.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseProfileContract;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;
import org.smartregister.view.presenter.BaseConfigurableMemberProfilePresenter;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseConfigurableMemberProfileActivity extends BaseProfileActivity implements BaseProfileContract.View {

    protected String moduleName;
    protected ModuleConfiguration moduleConfiguration;
    protected CommonPersonObjectClient client;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    protected CircleImageView profileImageView;
    private TextView tvNameAndAge;
    private TextView tvPrimaryCareGiver;
    private TextView tvGender;
    private TextView tvAddress;
    private TextView tvId;

    private View sickChildRegistrationRow;
    private View medicalHistoryRow;
    private View upcomingServicesRow;
    private View familyProfileRow;

    private TextView tvChildRegistrationDaysAgo;
    private TextView tvLastVisitDaysAgo;
    private TextView tvUpcomingServicesDue;
    private TextView tvFamilyDueServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        moduleName = Utils.extractModuleName(getIntent());
        moduleConfiguration = getModuleConfiguration();
        super.onCreate(savedInstanceState);
    }

    private ModuleConfiguration getModuleConfiguration() {
        return CoreLibrary.getInstance()
                .getModuleConfiguration(getModuleName());
    }

    public String getModuleName() {
        return moduleName;
    }

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_base_configurable_member_profile);

        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        // Set collapsing tool bar title.
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();
        setupViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            client = (CommonPersonObjectClient) getIntent().getSerializableExtra(AllConstants.INTENT_KEY.COMMON_PERSON_CLIENT);
        }
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        profileImageView = findViewById(R.id.profile_imageview);
        tvNameAndAge = findViewById(R.id.textview_name_age);
        tvPrimaryCareGiver = findViewById(R.id.textview_primary_caregiver);
        tvGender = findViewById(R.id.textview_gender);
        tvAddress = findViewById(R.id.textview_address);
        tvId = findViewById(R.id.textview_id);
        tvChildRegistrationDaysAgo = findViewById(R.id.text_view_child_reg_days_ago);
        tvLastVisitDaysAgo = findViewById(R.id.text_view_last_visit_days_ago);
        tvUpcomingServicesDue = findViewById(R.id.text_view_upcoming_services_due);
        tvFamilyDueServices = findViewById(R.id.text_view_family_profile_services_due);

        sickChildRegistrationRow = findViewById(R.id.sick_child_reg_row);
        sickChildRegistrationRow.setOnClickListener(v -> openSickChildRegistration());

        medicalHistoryRow = findViewById(R.id.medical_history_row);
        medicalHistoryRow.setOnClickListener(v -> openMedicalHistory());

        upcomingServicesRow = findViewById(R.id.upcoming_services_row);
        upcomingServicesRow.setOnClickListener(v -> openUpcomingServices());

        upcomingServicesRow = findViewById(R.id.upcoming_services_row);
        upcomingServicesRow.setOnClickListener(v -> openUpcomingServices());

        familyProfileRow = findViewById(R.id.family_profile_row);
        familyProfileRow.setOnClickListener(v -> openFamilyDueServices());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO -> Get menu from Config
        return true;
    }

    @Override
    protected BaseConfigurableMemberProfilePresenter initializePresenter() {
        return new BaseConfigurableMemberProfilePresenter(getModuleConfiguration(), this);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }

    @Override
    public ConfigurableMemberProfileActivityContract.Presenter presenter() {
        if (presenter == null) {
            initializePresenter();
        }
        return presenter;
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        // TODO -> Util.getImage(baseEntityId);
        imageRenderHelper.refreshProfileImage(baseEntityId, profileImageView, R.drawable.ic_child_care);
    }

    @Override
    public void setProfileName(String fullName) {
        tvNameAndAge.setText(fullName);
    }

    @Override
    public void setGender(String gender) {
        tvGender.setText(gender);
    }

    @Override
    public void setAddress(String address) {
        tvAddress.setText(address);
    }

    @Override
    public void setId(String id) {
        tvId.setText(id);
    }

    @Override
    public void setPrimaryCaregiver(String fullName) {
        tvPrimaryCareGiver.setText(fullName);
    }

    @Override
    public void startFormActivity(JSONObject formJson) {

    }

    @Override
    public void openMedicalHistory() {

    }

    @Override
    public void openUpcomingServices() {

    }

    @Override
    public void openFamilyDueServices() {

    }
}
