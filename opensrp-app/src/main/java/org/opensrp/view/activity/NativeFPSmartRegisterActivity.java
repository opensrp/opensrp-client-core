package org.opensrp.view.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import org.opensrp.AllConstants;
import org.opensrp.R;
import org.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.opensrp.domain.form.FieldOverrides;
import org.opensrp.provider.FPSmartRegisterClientsProvider;
import org.opensrp.provider.SmartRegisterClientsProvider;
import org.opensrp.view.contract.FPClient;
import org.opensrp.view.contract.SmartRegisterClient;
import org.opensrp.view.controller.*;
import org.opensrp.view.dialog.*;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static org.opensrp.AllConstants.FormNames.*;

public class NativeFPSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private static final String DIALOG_TAG = "dialog";
    private static int CHECKED_TAB_ID = R.id.rb_fp_method;

    private SmartRegisterClientsProvider clientProvider = null;
    private FPSmartRegisterController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new FPAllMethodsServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.fp_register_title_in_short);
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                Iterable<? extends DialogOption> villageFilterOptions =
                        dialogOptionMapper.mapToVillageFilterOptions(villageController.getVillages());
                return toArray(concat(DEFAULT_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {

                return new DialogOption[]{
                        new FPAllMethodsServiceMode(clientsProvider()),
                        new FPCondomServiceMode(clientsProvider()),
                        new FPDMPAServiceMode(clientsProvider()),
                        new FPIUCDServiceMode(clientsProvider()),
                        new FPOCPServiceMode(clientsProvider()),
                        new FPFemaleSterilizationServiceMode(clientsProvider()),
                        new FPMaleSterilizationServiceMode(clientsProvider()),
                        new FPOthersServiceMode(clientsProvider())
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new ECNumberSort(),
                        new HighPrioritySort(), new BPLSort(),
                        new SCSort(), new STSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_fp_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new FPSmartRegisterClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    private DialogOption[] getUpdateOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_fp_change_form), FP_CHANGE, formController),
                new OpenFormOption(getString(R.string.str_record_ecp_form), RECORD_ECPS, formController),
        };
    }

    @Override
    protected void onInitialization() {
        controller = new FPSmartRegisterController(context().allEligibleCouples(),
                context().allBeneficiaries(), context().alertService(),
                context().listCache(), context().fpClientsCache());
        villageController = new VillageController(context().allEligibleCouples(),
                context().listCache(), context().villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
        clientsProvider().onServiceModeSelected(new FPAllMethodsServiceMode(clientsProvider()));
    }

    @Override
    public void setupViews() {
        super.setupViews();

    }

    @Override
    public void startRegistration() {
        FieldOverrides fieldOverrides = new FieldOverrides(context().anmLocationController()
                .getLocationJSON());
        startFormActivity(EC_REGISTRATION, null, fieldOverrides.getJSONString());
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.profile_info_layout) {
                showProfileView((FPClient) view.getTag());

            } else if (i == R.id.btn_fp_method_update) {
                NativeFPSmartRegisterActivity.super.showFragmentDialog(new UpdateDialogOptionModel(), view.getTag());

            } else if (i == R.id.btn_side_effects) {
                SmartRegisterClient fpClient = (SmartRegisterClient) view.getTag();
                startFormActivity(FP_COMPLICATIONS, fpClient.entityId(), null);

            } else if (i == R.id.lyt_fp_add) {
                NativeFPSmartRegisterActivity.super.showFragmentDialog(new UpdateDialogOptionModel(), view.getTag());

            } else if (i == R.id.lyt_fp_videos) {
                navigationController.startVideos();

            }
        }

        private void showProfileView(FPClient client) {
            navigationController.startEC(client.entityId());
        }
    }

    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }

        if (!(dialogOptionModel instanceof  ServiceModeDialogOptionModel)) {
            NativeFPSmartRegisterActivity.super.showFragmentDialog(dialogOptionModel, tag);
            return;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogOptionModel = new FPServiceModeDialogOptionModel().cloneDialogOptionsWith(dialogOptionModel);
        FPSmartRegisterDialogFragment fpSmartRegisterDialogFragment = FPSmartRegisterDialogFragment
                .newInstance(this, dialogOptionModel, tag);
        fpSmartRegisterDialogFragment.setSelectedListener(listener);
        fpSmartRegisterDialogFragment.setArguments(setAndReturnTabSelectionBundle());
        fpSmartRegisterDialogFragment.show(ft, DIALOG_TAG);

    }

    private class UpdateDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getUpdateOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    private class FPServiceModeDialogOptionModel implements FPDialogOptionModel {
        private DialogOption[] parentDialogOption;
        @Override
        public DialogOption[] getPrioritizationDialogOptions() {
            return new DialogOption[]{
                    new FPPrioritizationAllECServiceMode(clientsProvider()),
                    new FPPrioritizationHighPriorityServiceMode(clientsProvider()),
                    new FPPrioritizationTwoPlusChildrenServiceMode(clientsProvider()),
                    new FPPrioritizationOneChildrenServiceMode(clientsProvider()),
            };
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return parentDialogOption;
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            NativeFPSmartRegisterActivity.super.onServiceModeSelection((ServiceModeOption) option);
        }

        public FPDialogOptionModel cloneDialogOptionsWith(DialogOptionModel dialogOptionModel) {
            this.parentDialogOption = dialogOptionModel.getDialogOptions();
            return this;
        }
    }

    private Bundle setAndReturnTabSelectionBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(AllConstants.FP_DIALOG_TAB_SELECTION, CHECKED_TAB_ID);
        return bundle;
    }

    FPSmartRegisterDialogFragment.onSelectedListener listener = new FPSmartRegisterDialogFragment.onSelectedListener() {
        @Override
        public void onSelected(int id) {
            CHECKED_TAB_ID = id;
        }
    };

}
