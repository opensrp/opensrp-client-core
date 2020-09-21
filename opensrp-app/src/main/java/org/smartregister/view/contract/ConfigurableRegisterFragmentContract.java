package org.smartregister.view.contract;

import org.json.JSONArray;
import org.smartregister.domain.Response;

import java.util.List;
import java.util.Set;

public interface ConfigurableRegisterFragmentContract {

    interface View {

        void initializeAdapter(Set<IView> visibleColumns);

        Presenter presenter();

    }

    interface Presenter {

        default void updateSortAndFilter(List<IField> filterList, IField sortField) {}

        default String getMainCondition() { return null; }

        default String getDefaultSortQuery() { return null; }

        default String getQueryTable()  {return null; }
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
