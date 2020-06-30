package org.smartregister.view.activity.mock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.json.JSONObject;
import org.smartregister.R;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Created by samuelgithengi on 6/30/20.
 */
public class BaseRegisterActivityMock extends BaseRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBarAndTitle);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initializePresenter() {
        presenter = mock(BaseRegisterContract.Presenter.class);
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new BaseRegisterFragmentMock();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {

    }

    @Override
    public void startFormActivity(JSONObject form) {

    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {

    }

    public static class BaseRegisterFragmentMock extends BaseRegisterFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initializePresenter();
        }

        @Override
        protected void initializePresenter() {
            presenter = mock(BaseRegisterFragmentContract.Presenter.class);
        }

        @Override
        public void setUniqueID(String qrCode) {

        }

        @Override
        public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {

        }

        @Override
        protected String getMainCondition() {
            return null;
        }

        @Override
        protected String getDefaultSortQuery() {
            return null;
        }

        @Override
        protected void startRegistration() {

        }

        @Override
        protected void onViewClicked(View view) {

        }

        @Override
        public void showNotFoundPopup(String opensrpId) {

        }
    }
}
