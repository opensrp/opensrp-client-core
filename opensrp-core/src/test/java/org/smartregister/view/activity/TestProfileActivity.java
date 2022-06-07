package org.smartregister.view.activity;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import org.smartregister.R;

/**
 * Created by Vincent Karuri on 13/04/2021
 */
public class TestProfileActivity extends BaseProfileActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializePresenter() {
        // do nothing
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        // do nothing
    }
}
