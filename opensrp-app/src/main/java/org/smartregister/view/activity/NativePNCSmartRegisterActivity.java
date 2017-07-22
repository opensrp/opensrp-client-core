package org.smartregister.view.activity;

import android.view.View;

import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.provider.PNCSmartRegisterClientsProvider;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.controller.PNCSmartRegisterController;
import org.smartregister.view.controller.VillageController;
import org.smartregister.view.dialog.*;

import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.toArray;
import static java.util.Arrays.asList;
import static org.smartregister.AllConstants.FormNames.*;

public class NativePNCSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final List<? extends DialogOption> DEFAULT_PNC_FILTER_OPTIONS =
            asList(new OutOfAreaFilter());
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private SmartRegisterClientsProvider clientProvider = null;
    private PNCSmartRegisterController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new PNCOverviewServiceMode(clientsProvider());
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
                return getResources().getString(R.string.pnc_register_title_in_short);
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
                return toArray(concat(DEFAULT_FILTER_OPTIONS, DEFAULT_PNC_FILTER_OPTIONS, villageFilterOptions), DialogOption.class);
            }

            @Override
            public DialogOption[] serviceModeOptions() {

                return new DialogOption[]{
                        new PNCOverviewServiceMode(clientsProvider()),
                        new PNCVisitsServiceMode(clientsProvider()),
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{new NameSort(), new DateOfDeliverySort(),
                        new HighRiskSort(), new BPLSort(),
                        new SCSort(), new STSort()};
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_pnc_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new PNCSmartRegisterClientsProvider(
                    this, clientActionHandler, controller);
        }
        return clientProvider;
    }

    private DialogOption[] getUpdateOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_pnc_visit_form), PNC_VISIT, formController),
                new OpenFormOption(getString(R.string.str_pnc_postpartum_family_planning_form), PNC_POSTPARTUM_FAMILY_PLANNING, formController),
                new OpenFormOption(getString(R.string.str_pnc_close_form), PNC_CLOSE, formController),
        };
    }

    @Override
    protected void onInitialization() {
        controller = new PNCSmartRegisterController(context().serviceProvidedService(),
                context().alertService(), context().allEligibleCouples(),
                context().allBeneficiaries(),
                context().listCache(), context().pncClientsCache());
        villageController = new VillageController(context().allEligibleCouples(),
                context().listCache(), context().villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
        clientsProvider().onServiceModeSelected(new PNCOverviewServiceMode(clientsProvider()));
    }

    @Override
    public void setupViews() {
        super.setupViews();
    }

    @Override
    public void startRegistration() {
        FieldOverrides fieldOverrides = new FieldOverrides(context().anmLocationController()
                .getLocationJSON());
        startFormActivity(PNC_REGISTRATION_OA, null, fieldOverrides.getJSONString());
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.profile_info_layout) {
                showProfileView((SmartRegisterClient) view.getTag());

            } else if (i == R.id.btn_edit) {
                showFragmentDialog(new UpdateDialogOptionModel(), view.getTag());

            }
        }

        private void showProfileView(SmartRegisterClient client) {
            navigationController.startPNC(client.entityId());
        }
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
}
