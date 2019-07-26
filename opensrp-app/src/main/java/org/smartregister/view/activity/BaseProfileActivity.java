package org.smartregister.view.activity;

import android.app.ProgressDialog;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.smartregister.R;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseProfileContract;

/**
 * Created by ndegwamartin on 16/07/2018.
 */
public abstract class BaseProfileActivity extends SecuredActivity implements BaseProfileContract.View, AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;

    protected String patientName;
    protected AppBarLayout appBarLayout;
    protected ProgressDialog progressDialog;

    protected BaseProfileContract.Presenter presenter;
    protected ImageRenderHelper imageRenderHelper;

    @Override
    protected void onCreation() {

        setContentView(R.layout.activity_base_profile);

        findViewById(R.id.btn_profile_registration_info).setOnClickListener(this);

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
    }

    @Override
    protected void onResumption() {
        //TODO Implement this
    }

    @Override
    public void onClick(View view) {
        fetchProfileData();
    }

    protected abstract void initializePresenter();

    protected void setupViews() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(setupViewPager(viewPager));
    }

    protected abstract ViewPager setupViewPager(ViewPager viewPager);

    protected abstract void fetchProfileData();

    public AppBarLayout getProfileAppBarLayout() {
        return appBarLayout;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (appBarLayoutScrollRange == -1) {
            appBarLayoutScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (appBarLayoutScrollRange + verticalOffset == 0) {

            collapsingToolbarLayout.setTitle(patientName);
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            collapsingToolbarLayout.setTitle(" ");
            appBarTitleIsShown = false;
        }

    }

    public void showProgressDialog(int saveMessageStringIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(saveMessageStringIdentifier));
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }
        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public String getIntentString(String intentKey) {

        return this.getIntent().getStringExtra(intentKey);
    }

    @Override
    public void displayToast(int stringID) {
        Utils.showShortToast(this, this.getString(stringID));
    }

}
