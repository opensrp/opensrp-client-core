package org.ei.opensrp.view.contract;


import org.ei.opensrp.domain.ANCServiceType;
import org.joda.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;

public interface ANCSmartRegisterClient extends SmartRegisterClient {

    Comparator<SmartRegisterClient> EDD_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient client, SmartRegisterClient anotherClient) {
            return ((ANCSmartRegisterClient) client).edd()
                    .compareTo(((ANCSmartRegisterClient) anotherClient).edd());
        }
    };

    public String eddForDisplay();

    LocalDateTime edd();

    public String pastDueInDays();

    public String weeksAfterLMP();

    public AlertDTO getAlert(ANCServiceType type);

    public boolean isVisitsDone();

    public boolean isTTDone();

    public boolean isIFADone();

    public String visitDoneDateWithVisitName();

    public String ttDoneDate();

    public String ifaDoneDate();

    public String thayiCardNumber();

    public String ancNumber();

    public String lmp();

    public String riskFactors();

    public ServiceProvidedDTO serviceProvidedToACategory(String category);

    public String getHyperTension(ServiceProvidedDTO ancServiceProvided);

    public ServiceProvidedDTO getServiceProvidedDTO(String serviceName);

    public List<ServiceProvidedDTO> allServicesProvidedForAServiceType(String serviceType);

    public String ashaPhoneNumber();
}
