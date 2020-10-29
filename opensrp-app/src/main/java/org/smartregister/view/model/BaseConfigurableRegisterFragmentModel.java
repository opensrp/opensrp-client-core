package org.smartregister.view.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.contract.IField;
import org.smartregister.view.contract.IView;
import org.smartregister.view.contract.IViewConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseConfigurableRegisterFragmentModel implements BaseRegisterFragmentContract.Model {

    @Override
    public IViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return null;
    }

    @Override
    public Set<IView> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return null;
    }

    @Override
    public String countSelect(String tableName, String mainCondition) {
        return null;
    }

    @Override
    public String mainSelect(String tableName, String mainCondition) {
        return null;
    }

    @Override
    public String getFilterText(List<IField> list, String filterTitle) {
        List<IField> filterList = list;
        if (filterList == null) {
            filterList = new ArrayList<>();
        }

        String filter = filterTitle;
        if (filter == null) {
            filter = "";
        }
        return "<font color=#727272>" + filter + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
    }

    @Override
    public String getSortText(IField sortField) {
        String sortText = "";
        if (sortField != null) {
            if (StringUtils.isNotBlank(sortField.getDisplayName())) {
                sortText = "(Sort: " + sortField.getDisplayName() + ")";
            } else if (StringUtils.isNotBlank(sortField.getDbAlias())) {
                sortText = "(Sort: " + sortField.getDbAlias() + ")";
            }
        }
        return sortText;
    }

    @Override
    public JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

}