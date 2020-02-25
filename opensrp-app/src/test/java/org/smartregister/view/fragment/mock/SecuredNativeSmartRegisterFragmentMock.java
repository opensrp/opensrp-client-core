package org.smartregister.view.fragment.mock;

import android.view.View;
import android.view.ViewGroup;

import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewholder.OnClickFormLauncher;

/**
 * Created by ndegwamartin on 2020-02-25.
 */
public class SecuredNativeSmartRegisterFragmentMock extends SecuredNativeSmartRegisterFragment {
    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {

                SmartRegisterClientsProvider provider = new SmartRegisterClientsProvider() {
                    @Override
                    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
                        return null;
                    }

                    @Override
                    public SmartRegisterClients getClients() {
                        return null;
                    }

                    @Override
                    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
                        return null;
                    }

                    @Override
                    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

                    }

                    @Override
                    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
                        return null;
                    }
                };

                return new ServiceModeOption(provider) {
                    @Override
                    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
                        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
                            @Override
                            public int count() {
                                return 0;
                            }

                            @Override
                            public int weightSum() {
                                return 0;
                            }

                            @Override
                            public int[] weights() {
                                return new int[0];
                            }

                            @Override
                            public int[] headerTextResourceIds() {
                                return new int[0];
                            }
                        };
                    }

                    @Override
                    public String name() {
                        return null;
                    }
                };
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
                return null;
            }
        };

    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return new SmartRegisterClientsProvider() {
            @Override
            public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
                return null;
            }

            @Override
            public SmartRegisterClients getClients() {
                return null;
            }

            @Override
            public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
                return null;
            }

            @Override
            public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

            }

            @Override
            public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
                return null;
            }
        };
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
