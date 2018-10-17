package org.smartregister.view.contract;

import android.content.Context;

import org.json.JSONArray;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseRegisterFragmentContract {

    interface View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void initializeQueryParams(String tableName, String countSelect, String mainSelect);

        void countExecute();

        void filterandSortInInitializeQueries();

        void updateSearchBarHint(String searchBarText);

        Context getContext();

        String getString(int resId);

        void updateFilterAndFilterStatus(String filterText, String sortText);

        void recalculatePagination(AdvancedMatrixCursor matrixCursor);

        void showProgressView();

        void hideProgressView();

        void showNotFoundPopup(String whoAncId);
    }

    interface Presenter {

        void processViewConfigurations();

        void initializeQueries(String mainCondition);

        void startSync();

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        void searchGlobally(String ancId);

        AdvancedMatrixCursor getMatrixCursor();
    }

    interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        Map<String, String> createEditMap(String ancId);

        AdvancedMatrixCursor createMatrixCursor(Response<String> response);

        JSONArray getJsonArray(Response<String> response);

    }


}
