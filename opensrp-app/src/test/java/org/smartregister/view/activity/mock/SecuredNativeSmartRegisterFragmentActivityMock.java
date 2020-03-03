package org.smartregister.view.activity.mock;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import org.smartregister.R;
import org.smartregister.adapter.SmartRegisterPaginatedAdapter;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.util.mock.SmartRegisterClientProviderMock;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.fragment.mock.SecuredNativeSmartRegisterFragmentMock;

/**
 * Created by kaderchowdhury on 14/11/17.
 */

public class SecuredNativeSmartRegisterFragmentActivityMock extends SecuredNativeSmartRegisterActivity {

    @Override
    public void onCreate(Bundle bundle) {
        setTheme(R.style.AppTheme); //we need this here
        super.onCreate(bundle);
        startFragment();
    }

    public void startFragment() {
        SecuredNativeSmartRegisterFragmentMock fragment = new SecuredNativeSmartRegisterFragmentMock();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, "");
        fragmentTransaction.commit();
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                SmartRegisterClientsProvider provider = new SmartRegisterClientProviderMock();


                return new ServiceModeOption(provider) {
                    @Override
                    public ClientsHeaderProvider getHeaderProvider() {
                        return new ClientsHeaderProvider() {
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
                        return new SmartRegisterClients();
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
    protected NavBarOptionsProvider getNavBarOptionsProvider() {

        return new NavBarOptionsProvider() {
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
        return new SmartRegisterClientProviderMock();
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    public void startRegistration() {

    }
}
