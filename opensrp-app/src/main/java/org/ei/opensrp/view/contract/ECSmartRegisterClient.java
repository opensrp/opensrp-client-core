package org.ei.opensrp.view.contract;


import org.ei.opensrp.domain.FPMethod;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface ECSmartRegisterClient extends BaseFPSmartRegisterClient, ECSmartRegisterBaseClient {

    Comparator<SmartRegisterClient> EC_NUMBER_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return ((ECSmartRegisterClient) client).ecNumber()
                    .compareTo(((ECSmartRegisterClient) anotherClient).ecNumber());
        }
    };

    public String numberOfPregnancies();

    public String parity();

    public String numberOfLivingChildren();

    public String numberOfStillbirths();

    public String numberOfAbortions();

    public String familyPlanningMethodChangeDate();

    public String numberOfOCPDelivered();

    public String numberOfCondomsSupplied();

    public String numberOfCentchromanPillsDelivered();

    public String iudPerson();

    public String iudPlace();

    public FPMethod fpMethod();

    public List<ECChildClient> children();

    public Map<String, String> status();
}
