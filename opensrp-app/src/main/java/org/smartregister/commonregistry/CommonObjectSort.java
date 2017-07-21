package org.smartregister.commonregistry;

import android.support.annotation.NonNull;

import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.SortOption;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Raihan Ahmed on 3/22/15.
 */
class CommonObjectSort implements SortOption {


    private String field;
    private ByColumnAndByDetails byColumnAndByDetails;
    private boolean isInteger;
    private String sortOptionName;
    public enum ByColumnAndByDetails{
        byColumn, byDetails
    }

    CommonObjectSort(ByColumnAndByDetails byColumnAndByDetailsArg,
                     boolean isIntegerArg,
                     String fieldArg,
                     String sortOptionNameArg) {
        byColumnAndByDetails = byColumnAndByDetailsArg;
        isInteger = isIntegerArg;
        field = fieldArg;
        sortOptionName = sortOptionNameArg;
    }

    @Override
    public String name() {
        return sortOptionName;
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, commonComparator);
        return allClients;
    }

    private Comparator<SmartRegisterClient> commonComparator = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient smartRegisterClient, SmartRegisterClient smartRegisterClient2) {
            CommonPersonObjectClient client = (CommonPersonObjectClient) smartRegisterClient;
            CommonPersonObjectClient client2 = (CommonPersonObjectClient) smartRegisterClient2;

            switch (byColumnAndByDetails) {
                case byColumn:
                    String columnFieldValue = getFieldValue(client, isInteger, false);
                    String columnFieldValue2 = getFieldValue(client2, isInteger, false);

                    if (!isInteger) {
                        return columnFieldValue.compareTo(columnFieldValue2);
                    } else {
                        return Integer.valueOf(columnFieldValue).compareTo(
                                Integer.valueOf(columnFieldValue2));

                    }
                case byDetails:
                    String detailFieldValue = getFieldValue(client, isInteger, true);
                    String detailFieldValue2 = getFieldValue(client2, isInteger, true);

                    if (!isInteger) {
                        return detailFieldValue.compareTo(detailFieldValue2);
                    } else {
                        return Integer.valueOf(detailFieldValue).compareTo(
                                Integer.valueOf(detailFieldValue2));
                    }
            }
            return 0;
        }
    };

    @NonNull
    private String getFieldValue(CommonPersonObjectClient commonPersonObjectClient,
                                 boolean isInteger,
                                 boolean isDetails) {
        String defaultValue = isInteger ? "0" : "";
        Map<String, String> valueMap = isDetails ? commonPersonObjectClient.getDetails()
                : commonPersonObjectClient.getColumnmaps();
        String detailsFieldValue = valueMap.get(field);

        return (detailsFieldValue != null ? detailsFieldValue : defaultValue).trim().toLowerCase();
    }
}
