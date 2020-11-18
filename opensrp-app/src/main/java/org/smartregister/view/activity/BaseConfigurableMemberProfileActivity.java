package org.smartregister.view.activity;

import androidx.viewpager.widget.ViewPager;

public class BaseConfigurableMemberProfileActivity extends BaseProfileActivity {

    @Override
    protected void onCreation() {
        //  TODO -> set dynamic content view, init views?
        initializePresenter();
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }
}
