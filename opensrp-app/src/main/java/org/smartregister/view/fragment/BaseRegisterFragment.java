package org.smartregister.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.FadingCircle;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseRegisterActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.activity.ProfileActivity;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.domain.AttentionFlag;
import org.smartregister.anc.event.SyncEvent;
import org.smartregister.anc.job.SyncServiceJob;
import org.smartregister.anc.job.SyncSettingsServiceJob;
import org.smartregister.anc.provider.RegisterProvider;
import org.smartregister.anc.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.NetworkUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by keyman on 26/06/2018.
 */

public abstract class BaseRegisterFragment extends RecyclerViewFragment implements RegisterFragmentContract.View,
        SyncStatusBroadcastReceiver.SyncStatusListener {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";
    public static final String CLICK_VIEW_SYNC = "click_view_sync";
    public static final String CLICK_VIEW_ATTENTION_FLAG = "click_view_attention_flag";
    private static final String TAG = BaseRegisterFragment.class.getCanonicalName();
    public static String TOOLBAR_TITLE = BaseRegisterActivity.class.getPackage() + ".toolbarTitle";
    protected RegisterActionHandler registerActionHandler = new RegisterActionHandler();
    protected RegisterFragmentContract.Presenter presenter;
    protected View rootView;
    protected TextView headerTextDisplay;
    protected TextView filterStatus;
    protected RelativeLayout filterRelativeLayout;
    protected View.OnKeyListener hideKeyboard = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Utils.hideKeyboard(getActivity(), v);
                return true;
            }
            return false;
        }
    };
    private ImageView qrCodeScanImageView;
    private ProgressBar syncProgressBar;
    private boolean globalQrSearch = false;
    private ImageView womanSyncButton;
    protected final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //Overriden Do something before Text Changed
        }

        @Override
        public void onTextChanged(final CharSequence cs, int start, int before, int count) {
            filter(cs.toString(), "", getMainCondition(), false);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //Overriden Do something after Text Changed
        }
    };

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {

        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public String searchHint() {
                return context().getStringResource(R.string.search_hint);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        rootView = view;//handle to the root

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activity.getSupportActionBar().setLogo(R.drawable.round_white_background);
        activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupViews(view);
        return view;
    }

    protected abstract void initializePresenter();

    protected void updateSearchView() {
        if (getSearchView() != null) {
            getSearchView().removeTextChangedListener(textWatcher);
            getSearchView().addTextChangedListener(textWatcher);
            getSearchView().setOnKeyListener(hideKeyboard);
        }
    }

    @Override
    public void updateSearchBarHint(String searchBarText) {
        if (getSearchView() != null) {
            getSearchView().setHint(searchBarText);
        }
    }

    public void setSearchTerm(String searchText) {
        if (getSearchView() != null) {
            getSearchView().setText(searchText);
        }
    }

    public void onQRCodeSucessfullyScanned(String qrCode) {
        Log.i(TAG, "QR code: " + qrCode);
        if (StringUtils.isNotBlank(qrCode)) {
            filter(qrCode.replace("-", ""), "", getMainCondition(), true);
            setAncId(qrCode);
        }
    }

    public void setAncId(String qrCode) {
        HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity) getActivity();
        android.support.v4.app.Fragment currentFragment =
                homeRegisterActivity.findFragmentByPosition(1);
        ((AdvancedSearchFragment) currentFragment).getAncId().setText(qrCode);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);

        presenter.processViewConfigurations();
        presenter.initializeQueries(getMainCondition());
        updateSearchView();
        setServiceModeViewDrawableRight(null);

        // QR Code
        qrCodeScanImageView = view.findViewById(R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity) getActivity();
                    if (homeRegisterActivity != null) {
                        homeRegisterActivity.startQrCodeScanner();
                    }
                }
            });
        }

        //Sync
        womanSyncButton = view.findViewById(R.id.woman_sync);
        if (womanSyncButton != null) {
            womanSyncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
                    SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
                }
            });
        }

        View topLeftLayout = view.findViewById(R.id.top_left_layout);
        if (topLeftLayout != null) {
            topLeftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrCodeScanImageView.performLongClick();
                }
            });
        }

        /*// Location
        facilitySelection = view.findViewById(R.id.facility_selection);
        if (facilitySelection != null) {m
            facilitySelection.init();
        }*/

        // Progress bar
        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        if (syncProgressBar != null) {
            FadingCircle circle = new FadingCircle();
            syncProgressBar.setIndeterminateDrawable(circle);
        }

        // Sort and Filter
        headerTextDisplay = view.findViewById(R.id.header_text_display);
        filterStatus = view.findViewById(R.id.filter_status);
        filterRelativeLayout = view.findViewById(R.id.filter_display_view);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        renderView();
    }

    private void renderView() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            presenter.initializeQueries(getMainCondition());
        }
        updateSearchView();
        presenter.processViewConfigurations();
        // updateLocationText();
        refreshSyncProgressSpinner();
        setTotalPatients();
    }

    private void setTotalPatients() {
        if (headerTextDisplay != null) {
            headerTextDisplay.setText(clientAdapter.getTotalcount() > 1 ?
                    String.format(getString(R.string.clients), clientAdapter.getTotalcount()) :
                    String.format(getString(R.string.client), clientAdapter.getTotalcount()));

            filterRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void initializeQueryParams(String tableName, String countSelect, String mainSelect) {
        this.tablename = tableName;
        this.mainCondition = getMainCondition();
        this.countSelect = countSelect;
        this.mainSelect = mainSelect;
        this.Sortqueries = DBConstants.KEY.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider = new RegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    public void filter(String filterString, String joinTableString, String mainConditionString, boolean qrCode) {
        getSearchCancelView().setVisibility(isEmpty(filterString) ? View.INVISIBLE : View.VISIBLE);

        this.filters = filterString;
        this.joinTable = joinTableString;
        this.mainCondition = mainConditionString;

        countExecute();

        if (qrCode && StringUtils.isNotBlank(filterString) && clientAdapter.getTotalcount() == 0 && NetworkUtils.isNetworkAvailable()) {
            globalQrSearch = true;
            presenter.searchGlobally(filterString);
        } else {
            filterandSortExecute();
        }

    }

    @Override
    public void updateFilterAndFilterStatus(String filterText, String sortText) {
        if (headerTextDisplay != null) {
            headerTextDisplay.setText(Html.fromHtml(filterText));
            filterRelativeLayout.setVisibility(View.VISIBLE);
        }

        if (filterStatus != null) {
            filterStatus.setText(Html.fromHtml(clientAdapter.getTotalcount() + " patients " + sortText));
        }
    }

    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        presenter.updateSortAndFilter(filterList, sortField);
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    protected void startRegistration() {
        ((HomeRegisterActivity) getActivity()).startFormActivity(Constants.JSON_FORM.ANC_REGISTER, null, null);
    }

    @Override
    protected void onCreation() {
        initializePresenter();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            boolean isRemote = extras.getBoolean(Constants.INTENT_KEY.IS_REMOTE_LOGIN);
            if (isRemote) {
                presenter.startSync();
            }
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    protected abstract String getMainCondition();

    private void goToPatientDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {
        if (launchDialog) {
            Log.i(BaseRegisterFragment.TAG, patient.name);
        }

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    /*protected void updateLocationText() {
        if (facilitySelection != null) {
            facilitySelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(
                    facilitySelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(facilitySelection.getSelectedItem());
            context().allSharedPreferences().savePreference(Constants.CURRENT_LOCATION_ID, locationId);

        }
    }

    public LocationPickerView getFacilitySelection() {
        return facilitySelection;
    }*/

    protected abstract void onViewClicked(View view);

    private void registerSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    private void unregisterSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        Utils.postEvent(new SyncEvent(fetchStatus));
        refreshSyncStatusViews(fetchStatus);
    }

    @Override
    public void onSyncStart() {
        refreshSyncStatusViews(null);
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        refreshSyncStatusViews(fetchStatus);
    }

    private void refreshSyncStatusViews(FetchStatus fetchStatus) {

        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            Utils.showShortToast(getActivity(), getString(R.string.syncing));
        } else {
            if (fetchStatus != null) {

                if (fetchStatus.equals(FetchStatus.fetchedFailed)) {

                    Utils.showShortToast(getActivity(), getString(R.string.sync_failed));

                } else if (fetchStatus.equals(FetchStatus.fetched)
                        || fetchStatus.equals(FetchStatus.nothingFetched)) {

                    setRefreshList(true);
                    renderView();

                    Utils.showShortToast(getActivity(), getString(R.string.sync_complete));

                } else if (fetchStatus.equals(FetchStatus.noConnection)) {

                    Utils.showShortToast(getActivity(), getString(R.string.sync_failed_no_internet));
                }
            }

        }

        refreshSyncProgressSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSyncStatusBroadcastReceiver();
    }

    @Override
    public void onPause() {
        unregisterSyncStatusBroadcastReceiver();
        super.onPause();
    }

    private void refreshSyncProgressSpinner() {
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(View.VISIBLE);
            }
            if (womanSyncButton != null) {
                womanSyncButton.setVisibility(View.GONE);
            }
        } else {
            if (syncProgressBar != null) {
                syncProgressBar.setVisibility(View.GONE);
            }
            if (womanSyncButton != null) {
                womanSyncButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        clientAdapter.setTotalcount(matrixCursor.getCount());
        Log.v("total count here", "" + clientAdapter.getTotalcount());
        clientAdapter.setCurrentlimit(20);
        if (clientAdapter.getTotalcount() > 0) {
            clientAdapter.setCurrentlimit(clientAdapter.getTotalcount());
        }
        clientAdapter.setCurrentoffset(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final AdvancedMatrixCursor matrixCursor = presenter.getMatrixCursor();
        if (!globalQrSearch || matrixCursor == null) {
            return super.onCreateLoader(id, args);
        } else {
            globalQrSearch = false;
            switch (id) {
                case LOADER_ID:
                    // Returns a new CursorLoader
                    return new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            return matrixCursor;
                        }
                    };
                default:
                    // An invalid id was passed in
                    return null;
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class RegisterActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
                goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
                ((HomeRegisterActivity) getActivity()).showRecordBirthPopUp((CommonPersonObjectClient) view.getTag());

            } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ATTENTION_FLAG) {
                //Temporary for testing UI , To remove for real dynamic data
                List<AttentionFlag> dummyAttentionFlags = Arrays.asList(new AttentionFlag[]{new AttentionFlag("Red Flag 1", true), new
                        AttentionFlag("Red Flag 2", true), new AttentionFlag("Yellow Flag 1", false), new AttentionFlag("Yellow Flag 2", false)});
                ((HomeRegisterActivity) getActivity()).showAttentionFlagsDialog(dummyAttentionFlags);
            } /*else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_SYNC) { // Need to implement move to catchment
                // TODO Move to catchment
            }*/ else {
                onViewClicked(view);
            }
        }
    }
}



