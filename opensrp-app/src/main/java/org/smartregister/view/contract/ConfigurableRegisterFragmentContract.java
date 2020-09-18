package org.smartregister.view.contract;

import org.json.JSONArray;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface ConfigurableRegisterFragmentContract {

    interface View {

        void initializeAdapter(Set<IView> visibleColumns);

        Presenter presenter();

    }

    interface Presenter {

        void updateSortAndFilter(List<IField> filterList, IField sortField);

        String getMainCondition();

        String getDefaultSortQuery();

        String getQueryTable();
    }

    interface Model {

        IViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<IView> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);

        String getFilterText(List<IField> filterList, String filter);

        String getSortText(IField sortField);

        JSONArray getJsonArray(Response<String> response);

    }
}
