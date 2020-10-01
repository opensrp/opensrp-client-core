package org.smartregister.view.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.configuration.ModuleMetadata;
import org.smartregister.configuration.ModuleRegisterQueryProviderContract;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.BaseRegisterProvider;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.ConfigurationInstancesHelper;
import org.smartregister.util.RegisterViewConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseConfigurableRegisterActivity;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterFragmentContract;
import org.smartregister.view.dialog.NoMatchDialogFragment;
import org.smartregister.view.model.BaseConfigurableRegisterFragmentModel;
import org.smartregister.view.presenter.BaseConfigurableRegisterFragmentPresenter;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class BaseConfigurableRegisterFragment extends BaseRegisterFragment {

    private static final String DUE_FILTER_TAG = "PRESSED";
    private View view;
    private View dueOnlyLayout;
    private boolean dueFilterActive = false;
    private ModuleRegisterQueryProviderContract moduleRegisterQueryProvider;
    private ModuleConfiguration moduleConfiguration;

    public BaseConfigurableRegisterFragment() {
    }

    public void setModuleConfiguration(@NonNull ModuleConfiguration moduleConfiguration) {
        moduleRegisterQueryProvider = ConfigurationInstancesHelper.newInstance(moduleConfiguration.getRegisterQueryProvider());
        this.moduleConfiguration = moduleConfiguration;
    }

    public ModuleConfiguration getModuleConfiguration() {
        return moduleConfiguration;
    }

    public void setModuleRegisterQueryProvider(ModuleRegisterQueryProviderContract moduleRegisterQueryProvider) {
        this.moduleRegisterQueryProvider = moduleRegisterQueryProvider;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        this.view = view;

        // Update top left icon
        qrCodeScanImageView = view.findViewById(org.smartregister.R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setVisibility(View.GONE);
        }

        // Update Search bar
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View searchBarLayout = view.findViewById(org.smartregister.R.id.search_bar_layout);
        searchBarLayout.setLayoutParams(params);
        searchBarLayout.setBackgroundResource(R.color.chw_primary);
        searchBarLayout.setPadding(searchBarLayout.getPaddingLeft()
                , searchBarLayout.getPaddingTop()
                , searchBarLayout.getPaddingRight()
                , (int) Utils.convertDpToPixel(10, getActivity()));


        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
            getSearchView().setTextColor(getResources().getColor(R.color.text_black));
        }

        // Update title name
        ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }

        TextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getToolBarTitle());
            //titleView.setFontVariant(FontVariant.REGULAR);
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        View topLeftLayout = view.findViewById(R.id.top_left_layout);
        topLeftLayout.setVisibility(View.GONE);

        View topRightLayout = view.findViewById(R.id.top_right_layout);
        topRightLayout.setVisibility(View.VISIBLE);

        View sortFilterBarLayout = view.findViewById(R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(View.GONE);

        View filterSortLayout = view.findViewById(R.id.filter_sort_layout);
        filterSortLayout.setVisibility(View.GONE);

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);

        ((TextView) view.findViewById(R.id.due_only_text_view)).setText(getDueOnlyText());

        topRightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        if (presenter == null) {
            presenter = new BaseConfigurableRegisterFragmentPresenter(getModuleConfiguration(), this, new BaseConfigurableRegisterFragmentModel());
        }
    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //// TODO: 15/08/19
    }

    @Override
    protected void onResumption() {
        if (dueFilterActive && dueOnlyLayout != null) {
            enableDueOnlyFilter(dueOnlyLayout, dueFilterActive);
        } else {
            super.onResumption();
        }
    }

    @Override
    protected String getMainCondition() {
        return mainCondition;
    }

    @Override
    protected String getDefaultSortQuery() {
        return "";
    }

    @Override
    protected void startRegistration() {
        // TODO: Implement this

        ModuleMetadata moduleMetadata = getModuleConfiguration().getModuleMetadata();
        // TODO: ADD RxJava for entityId
        if (getActivity() instanceof  BaseConfigurableRegisterActivity && moduleMetadata != null) {
            ((BaseConfigurableRegisterActivity) getActivity()).startFormActivity(moduleMetadata.getRegistrationFormName(), null, (String) null);
        }
    }

    @Override
    protected void onViewClicked(View view) {
        // TODO: Abstract
        if (getActivity() == null) {
            return;
        }

        if (view.getId() == R.id.due_only_layout) {
            toggleFilterSelection(view);
        } else if (view.getTag(R.id.VIEW_TYPE) != null) {
            Object viewClient = view.getTag(R.id.VIEW_CLIENT);

            if (viewClient != null) {
                if (viewClient instanceof CommonPersonObjectClient) {
                    if (view.getTag(R.id.VIEW_TYPE).equals(RegisterViewConstants.Provider.CHILD_COLUMN)) {

                        goToClientDetailActivity((CommonPersonObjectClient) viewClient);
                    } else if (view.getTag(R.id.VIEW_TYPE).equals(RegisterViewConstants.Provider.ACTION_BUTTON_COLUMN)) {
                        performPatientAction((CommonPersonObjectClient) viewClient);
                    }
                } else {
                    Timber.e(new Exception(), "Value for key[%d] is not a CommonPersonObjectClient but is of type %s"
                            , R.id.VIEW_CLIENT
                            , viewClient.getClass().getName());
                }
            }
        }
    }

    protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        // TODO: FINISH THIS
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) && dueFilterActive && dueOnlyLayout != null) {
            enableDueOnlyFilter(dueOnlyLayout, dueFilterActive);
            Utils.showShortToast(getActivity(), getString(R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncInProgress(fetchStatus);
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus)
                || FetchStatus.nothingFetched.equals(fetchStatus)) && (dueFilterActive && dueOnlyLayout != null)) {
            enableDueOnlyFilter(dueOnlyLayout, dueFilterActive);
            Utils.showShortToast(getActivity(), getString(R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncComplete(fetchStatus);
        }

        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseConfigurableRegisterActivity) {
            ConfigurationInstancesHelper.newInstance(
            ((BaseConfigurableRegisterActivity) activity)
                    .getModuleConfiguration()
                    .getActivityStarter())
                    .startProfileActivity(getActivity(), commonPersonObjectClient);
        }
    }

    protected void toggleFilterSelection(@Nullable View filterSection) {
        if (filterSection != null) {
            String tagString = "PRESSED";
            if (filterSection.getTag() == null) {
                filter(searchText(), "", presenter().getDueFilterCondition(), false);
                filterSection.setTag(tagString);
            } else if (filterSection.getTag().toString().equals(tagString)) {
                filter(searchText(), "", "", false);
                filterSection.setTag(null);
            }
        }
    }

    private void enableDueOnlyFilter(@NonNull View dueOnlyLayout, boolean enable) {
        String tag = enable ? DUE_FILTER_TAG : null;
        String mainConditionString = enable ? presenter().getDueFilterCondition() : "";

        filter(searchText(), "", mainConditionString);
        dueOnlyLayout.setTag(tag);
        switchViews(dueOnlyLayout, false);
    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        filterandSortExecute(countBundle());
    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        TextView dueOnlyTextView = dueOnlyLayout.findViewById(R.id.due_only_text_view);
        if (isPress) {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
        } else {
            dueOnlyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);

        }
    }

    @Override
    public void initializeAdapter() {
        BaseRegisterProvider childRegisterProvider = new BaseRegisterProvider(getActivity(), registerActionHandler, paginationViewHandler);
        //childRegisterProvider.setModuleName("custom-family");
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public BaseRegisterFragmentContract.Presenter presenter() {
        return (BaseRegisterFragmentContract.Presenter) presenter;
    }

    protected String getToolBarTitle() {
        return getModuleConfiguration().getRegisterTitle();
    }

    @Override
    public void showNotFoundPopup(String uniqueId) {
        if (getActivity() == null) {
            return;
        }
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) getActivity(), DIALOG_TAG, uniqueId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        if (id == LOADER_ID) {// Returns a new CursorLoader
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    // Select register query
                    String query = filterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }// An invalid id was passed in
        return null;
    }

    private String filterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = moduleRegisterQueryProvider.getObjectIdsQuery(filters, mainCondition);
                sql = sqb.addlimitandOffset(sql, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());

                List<String> ids = commonRepository().findSearchIds(sql);
                query = moduleRegisterQueryProvider.mainSelectWhereIDsIn();

                String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
                return query.replace("%s", joinedIds);
            } else {
                if (!TextUtils.isEmpty(filters) && TextUtils.isEmpty(Sortqueries)) {
                    sqb.addCondition(filters);
                    query = sqb.orderbyCondition(Sortqueries);
                    query = sqb.Endquery(sqb.addlimitandOffset(query
                            , clientAdapter.getCurrentlimit()
                            , clientAdapter.getCurrentoffset()));
                }

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    @Override
    public void countExecute() {
        try {
            int totalCount = 0;
            for (String sql : moduleRegisterQueryProvider.countExecuteQueries(filters, mainCondition)) {
                Timber.i(sql);
                totalCount += commonRepository().countSearchIds(sql);
            }

            clientAdapter.setTotalcount(totalCount);
            Timber.i("Total Register Count %d", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    @NonNull
    @Override
    public String getDueOnlyText() {
        return getString(R.string.due_only);
    }
}