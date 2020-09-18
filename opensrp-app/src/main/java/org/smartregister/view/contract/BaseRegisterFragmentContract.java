package org.smartregister.view.contract;

import android.content.Context;

public interface BaseRegisterFragmentContract {

    interface View extends ConfigurableRegisterFragmentContract.View {

        void initializeQueryParams(String tableName, String countSelect, String mainSelect);

        void countExecute();

        void filterandSortInInitializeQueries();

        void updateSearchBarHint(String searchBarText);

        Context getContext();

        String getString(int resId);

        void updateFilterAndFilterStatus(String filterText, String sortText);

        void showProgressView();

        void hideProgressView();

        void showNotFoundPopup(String opensrpId);

        void setTotalPatients();
    }

    interface Presenter extends ConfigurableRegisterFragmentContract.Presenter {

        void processViewConfigurations();

        void initializeQueries(String mainCondition);

        void startSync();

        void searchGlobally(String uniqueId);

    }

    interface Model extends ConfigurableRegisterFragmentContract.Model {

    }


}
