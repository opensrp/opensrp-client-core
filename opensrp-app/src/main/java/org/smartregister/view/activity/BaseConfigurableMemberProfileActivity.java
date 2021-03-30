package org.smartregister.view.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.adapter.ConfigurableMemberProfileRowAdapter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.BaseMemberProfileOptions;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.domain.ConfigurableMemberProfileRowData;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;
import org.smartregister.view.presenter.BaseConfigurableMemberProfilePresenter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseConfigurableMemberProfileActivity extends BaseProfileActivity implements ConfigurableMemberProfileActivityContract.View {

    protected String moduleName;
    protected ModuleConfiguration moduleConfiguration;
    protected CommonPersonObjectClient client;
    protected CircleImageView profileImageView;
    private TextView tvNameAndAge;
    private TextView tvPrimaryCareGiver;
    private TextView tvGender;
    private TextView tvAddress;
    private TextView tvId;
    private RecyclerView.Adapter profileRowAdapter;
    private ProgressBar progressBar;
    private List<ConfigurableMemberProfileRowData> rowDataList = new ArrayList<>();
    private BaseMemberProfileOptions memberProfileOptions;

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
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
            upArrow.setColorFilter(getResources().getColor(R.color.text_black), PorterDuff.Mode.SRC_ATOP);
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
        memberProfileOptions = presenter().getMemberProfileOptions();
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

        profileRowAdapter = new ConfigurableMemberProfileRowAdapter(this, rowDataList);

        RecyclerView recyclerView = findViewById(R.id.profile_bottom_recyclerView);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(profileRowAdapter);

        profileRowAdapter.notifyDataSetChanged();
        progressBar = findViewById(R.id.progress_bar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (memberProfileOptions != null && memberProfileOptions.getMenuLayoutId() > 0)
            getMenuInflater().inflate(memberProfileOptions.getMenuLayoutId(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (memberProfileOptions != null)
            memberProfileOptions.onMenuOptionsItemSelected(item, this, client);
        return super.onOptionsItemSelected(item);
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
        showProgressBar(true);
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
        imageRenderHelper.refreshProfileImage(baseEntityId, profileImageView, R.drawable.ic_member);
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
    public void goToRowActivity(Class<?> rowClickedLaunchedClass) {
        // TODO Implement
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateBottomSection(List<ConfigurableMemberProfileRowData> dataList) {
        showProgressBar(false);
        if (dataList == null)
            return;

        this.rowDataList.addAll(dataList);
        profileRowAdapter.notifyDataSetChanged();
    }
}
