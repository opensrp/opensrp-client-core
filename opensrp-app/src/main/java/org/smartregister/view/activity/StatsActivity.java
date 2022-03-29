package org.smartregister.view.activity;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.adapter.PagerAdapter;
import org.smartregister.util.LangUtils;
import org.smartregister.view.contract.StatsContract;
import org.smartregister.view.fragment.StatsFragment;

import java.util.Objects;

public class StatsActivity extends AppCompatActivity implements StatsContract.View {

    private ProgressDialog progressDialog;
    protected StatsFragment mBaseFragment = null;

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        Configuration newConfiguration = LangUtils.setAppLocale(base, lang);

        super.attachBaseContext(base);

        applyOverrideConfiguration(newConfiguration);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);

        Toolbar toolbar = this.findViewById(R.id.summary_toolbar);
        toolbar.setTitle(CoreLibrary.getInstance().context().applicationContext().getString(R.string.return_to_register));
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        setupViews();
    }

    protected void setupViews() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
    }

    protected ViewPager setupViewPager(ViewPager viewPager) {
        mBaseFragment = new StatsFragment();
        Fragment[] otherFragments = getOtherFragments();
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(CoreLibrary.getInstance().context().applicationContext().getString(R.string.please_wait_message));

        if (!isFinishing())
            progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected Fragment[] getOtherFragments() {
        Fragment[] fragments = new Fragment[1];
        fragments[0] = new StatsFragment();
        return fragments;
    }
}
