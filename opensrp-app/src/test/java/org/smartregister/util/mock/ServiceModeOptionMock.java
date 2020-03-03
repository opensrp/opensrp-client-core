package org.smartregister.util.mock;

import org.smartregister.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.ServiceModeOption;

/**
 * Created by ndegwamartin on 2020-03-03.
 */
public class ServiceModeOptionMock extends ServiceModeOption {
    private int[] weights = new int[]{3, 4};
    private int[] headerResourceIds = new int[]{R.string.header_name, R.string.header_ec_no};

    public ServiceModeOptionMock(SmartRegisterClientsProvider clientsProvider) {
        super(clientsProvider);
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 0;
            }

            @Override
            public int weightSum() {
                return 7;
            }

            @Override
            public int[] weights() {
                return weights;
            }

            @Override
            public int[] headerTextResourceIds() {
                return headerResourceIds;
            }
        };
    }

    @Override
    public String name() {
        return "ServiceModeOptionTestName";
    }
}
