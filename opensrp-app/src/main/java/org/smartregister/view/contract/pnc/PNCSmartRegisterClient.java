package org.smartregister.view.contract.pnc;


import org.smartregister.domain.ANCServiceType;
import org.smartregister.view.contract.*;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.List;

public interface PNCSmartRegisterClient extends BaseFPSmartRegisterClient {

    Comparator<SmartRegisterClient> DATE_OF_DELIVERY_COMPARATOR = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient lhs, SmartRegisterClient rhs) {
            return ((PNCSmartRegisterClient) rhs).deliveryDate().compareTo(((PNCSmartRegisterClient) lhs).deliveryDate());
        }
    };

    public String thayiNumber();

    public String deliveryDateForDisplay();

    public String deliveryShortDate();

    public LocalDate deliveryDate();

    public String deliveryPlace();

    public String deliveryType();

    public String deliveryComplications();

    public String womanDOB();

    public List<ChildClient> children();

    public List<PNCCircleDatum> pncCircleData();

    public List<PNCStatusDatum> pncStatusData();

    public PNCStatusColor pncVisitStatusColor();

    public List<PNCTickDatum> pncTickData();

    public List<PNCLineDatum> pncLineData();

    public List<PNCVisitDaysDatum> visitDaysData();

    public PNCFirstSevenDaysVisits firstSevenDaysVisits();

    public List<ServiceProvidedDTO> recentlyProvidedServices();

    boolean isVisitsDone();

    public String visitDoneDateWithVisitName();

    public AlertDTO getAlert(ANCServiceType serviceType);

    public String pncComplications();
}

