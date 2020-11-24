package org.smartregister.view.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.domain.AlertStatus;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;
import org.smartregister.view.presenter.BaseConfigurableMemberProfilePresenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseConfigurableMemberProfileActivity extends BaseProfileActivity implements ConfigurableMemberProfileActivityContract.View {

    protected String moduleName;
    protected ModuleConfiguration moduleConfiguration;
    protected CommonPersonObjectClient client;
    protected CircleImageView profileImageView;
    private TextView tvTitle;
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
        tvTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_action_goldsmith_menu_arrow);
            upArrow.setColorFilter(getResources().getColor(R.color.text_blue), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(null);
        }
        imageRenderHelper = new ImageRenderHelper(this);

        setupViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            client = (CommonPersonObjectClient) getIntent().getSerializableExtra(AllConstants.INTENT_KEY.COMMON_PERSON_CLIENT);
        }

        initializePresenter();
        presenter().fetchProfileData(client);
    }

    @Override
    protected void setupViews() {
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
    protected void initializePresenter() {
        presenter = new BaseConfigurableMemberProfilePresenter(getModuleConfiguration(), this);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData(client);
    }

    @Override
    public ConfigurableMemberProfileActivityContract.Presenter presenter() {
        if (presenter == null) {
            initializePresenter();
        }
        return (ConfigurableMemberProfileActivityContract.Presenter) presenter;
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
        // TODO
    }

    @Override
    public void setLastVisit(Date lastVisitDate) {
        if (lastVisitDate == null)
            return;

        if (lastVisitDate != null) {
            medicalHistoryRow.setVisibility(View.VISIBLE);
            int numOfDays = Days.daysBetween(new DateTime(lastVisitDate).toLocalDate(), new DateTime().toLocalDate()).getDays();
            tvLastVisitDaysAgo.setText(getString(R.string.last_visit_days_ago, (numOfDays <= 1) ? getString(R.string.less_than_twenty_four) : String.valueOf(numOfDays) + " " + getString(R.string.days)));
        }
    }

    @Override
    public void setUpComingServicesStatus(String service, AlertStatus status, Date date) {
        if (status == AlertStatus.complete)
            return;

        upcomingServicesRow.setVisibility(View.VISIBLE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        if (status == AlertStatus.upcoming) {
            tvUpcomingServicesDue.setText(Html.fromHtml(getString(R.string.service_upcoming, service, dateFormat.format(date))));
        } else if (status == AlertStatus.urgent) {
            tvUpcomingServicesDue.setText(Html.fromHtml(getString(R.string.service_overdue, service, dateFormat.format(date))));
        } else {
            tvUpcomingServicesDue.setText(Html.fromHtml(getString(R.string.service_due, service, dateFormat.format(date))));
        }
    }

    @Override
    public void setFamilyStatus(AlertStatus status) {
        familyProfileRow.setVisibility(View.VISIBLE);
        if (status == AlertStatus.complete) {
            tvFamilyDueServices.setText(getString(R.string.family_has_nothing_due));
        } else if (status == AlertStatus.normal) {
            tvFamilyDueServices.setText(getString(R.string.family_has_services_due));
        } else if (status == AlertStatus.urgent) {
            tvFamilyDueServices.setText(Html.fromHtml(getString(R.string.family_has_service_overdue)));
        }
    }

    @Override
    public void openMedicalHistory() {
        // TODO
    }

    @Override
    public void openUpcomingServices() {
        // TODO
    }

    @Override
    public void openFamilyDueServices() {
        // TODO
    }
}
