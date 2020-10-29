package org.smartregister.view.presenter;

import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.contract.IField;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-09-2020
 */

public class BaseConfigurableRegisterFragmentPresenter implements BaseRegisterFragmentContract.Presenter {

    private WeakReference<BaseRegisterFragmentContract.View> viewReference;
    private BaseRegisterFragmentContract.Model model;
    private ModuleConfiguration moduleConfiguration;

    public BaseConfigurableRegisterFragmentPresenter(ModuleConfiguration moduleConfiguration, BaseRegisterFragmentContract.View view, BaseRegisterFragmentContract.Model model) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.moduleConfiguration = moduleConfiguration;
    }

    @Override
    public void processViewConfigurations() {
        // Do nothing since we don't have process views
    }

    @Override
    public void initializeQueries(String mainCondition) {

        getView().initializeQueryParams(moduleConfiguration.getModuleMetadata().getTableName(), null, null);
        getView().initializeAdapter();

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    protected BaseRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {

            return null;
        }
    }

    @Override
    public void updateSortAndFilter(List<IField> filterList, IField sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getDueFilterCondition() {
        return "DUE_ONLY";
    }

    public void setModel(BaseRegisterFragmentContract.Model model) {
        this.model = model;
    }
}
