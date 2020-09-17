package org.smartregister.view.contract;

import org.json.JSONArray;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface ConfigurableRegisterFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        String getMainCondition();

        String getDefaultSortQuery();

        String getQueryTable();
    }

    interface Model {

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        JSONArray getJsonArray(Response<String> response);

    }
}
