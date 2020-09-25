package org.smartregister.view.presenter;

import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.contract.IField;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 25-09-2020
 */

public class OpdRegisterFragmentPresenter implements BaseRegisterFragmentContract.Presenter {

    private WeakReference<BaseRegisterFragmentContract.View> viewReference;
    private BaseRegisterFragmentContract.Model model;
    private ModuleConfiguration moduleConfiguration;

    public OpdRegisterFragmentPresenter(ModuleConfiguration moduleConfiguration, BaseRegisterFragmentContract.View view, BaseRegisterFragmentContract.Model model) {
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
        /*QueryTable childQueryTable = new QueryTable();
        childQueryTable.setTableName("ec_child");
        childQueryTable.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "gender",
                "home_address",
                "'Child' AS register_type",
                "relational_id AS relationalid",
                "last_interacted_with"
        });

        QueryTable womanQueryTable = new QueryTable();
        womanQueryTable.setTableName("ec_mother");
        womanQueryTable.setColNames(new String[]{
                "first_name",
                "last_name",
                "middle_name",
                "'Female' AS gender",
                "home_address",
                "'ANC' AS register_type",
                "NULL AS mother_first_name",
                "NULL AS mother_last_name",
                "NULL AS mother_middle_name",
                "relationalid",
                "last_interacted_with"
        });

        InnerJoinObject[] innerJoinObjects = new InnerJoinObject[1];
        InnerJoinObject childTableInnerJoinMotherTable = new InnerJoinObject();
        childTableInnerJoinMotherTable.setFirstTable(childQueryTable);

        QueryTable innerJoinMotherTable = new QueryTable();
        innerJoinMotherTable.setTableName("ec_mother");
        innerJoinMotherTable.setColNames(new String[]{
                "first_name AS mother_first_name",
                "last_name AS mother_last_name",
                "middle_name AS mother_middle_name"
        });

        childTableInnerJoinMotherTable.innerJoinOn("ec_child.relational_id = ec_mother.base_entity_id");
        childTableInnerJoinMotherTable.innerJoinTable(innerJoinMotherTable);
        innerJoinObjects[0] = childTableInnerJoinMotherTable;

        String mainSelect = model.mainSelect(innerJoinObjects, new QueryTable[]{womanQueryTable});*/

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
