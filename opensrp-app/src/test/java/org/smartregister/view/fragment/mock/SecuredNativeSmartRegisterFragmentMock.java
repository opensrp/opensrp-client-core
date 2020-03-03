package org.smartregister.view.fragment.mock;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.mock.ServiceModeOptionMock;
import org.smartregister.util.mock.SmartRegisterClientProviderMock;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;

/**
 * Created by ndegwamartin on 2020-02-25.
 */
public class SecuredNativeSmartRegisterFragmentMock extends SecuredNativeSmartRegisterFragment {

    public static final String TEST_SEARCH_HINT = "Test Search Hint";

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {


                SmartRegisterClientsProvider provider = new SmartRegisterClientProviderMock();

                return new ServiceModeOptionMock(provider);
            }

            @Override
            public FilterOption villageFilter() {
                return new FilterOption() {
                    @Override
                    public boolean filter(SmartRegisterClient client) {
                        return false;
                    }

                    @Override
                    public String name() {
                        return null;
                    }
                };
            }

            @Override
            public SortOption sortOption() {
                return new SortOption() {
                    @Override
                    public SmartRegisterClients sort(SmartRegisterClients allClients) {
                        return null;
                    }

                    @Override
                    public String name() {
                        return null;
                    }
                };
            }

            @Override
            public String nameInShortFormForTitle() {
                return null;
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {

        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {
            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[0];
            }

            @Override
            public String searchHint() {
                return TEST_SEARCH_HINT;
            }
        };

    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return new SmartRegisterClientProviderMock();
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onCreation() {

    }
}
