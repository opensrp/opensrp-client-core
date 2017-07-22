package org.smartregister.view.contract.pnc;

import org.smartregister.domain.ANCServiceType;
import org.smartregister.view.contract.*;
import org.joda.time.LocalDate;

import java.util.Comparator;
import java.util.List;

public interface PNCSmartRegisterClient extends BaseFPSmartRegisterClient {

    Comparator<SmartRegisterClient> DATE_OF_DELIVERY_COMPARATOR = new
            Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient lhs, SmartRegisterClient rhs) {
            return ((PNCSmartRegisterClient) rhs).deliveryDate()
                    .compareTo(((PNCSmartRegisterClient) lhs).deliveryDate());
        }
    };

    String thayiNumber();

    String deliveryDateForDisplay();

    String deliveryShortDate();

    LocalDate deliveryDate();

    String deliveryPlace();

    String deliveryType();

    String deliveryComplications();

    String womanDOB();

    List<ChildClient> children();

    List<PNCCircleDatum> pncCircleData();

    List<PNCStatusDatum> pncStatusData();

    PNCStatusColor pncVisitStatusColor();

    List<PNCTickDatum> pncTickData();

    List<PNCLineDatum> pncLineData();

    List<PNCVisitDaysDatum> visitDaysData();

    PNCFirstSevenDaysVisits firstSevenDaysVisits();

    List<ServiceProvidedDTO> recentlyProvidedServices();

    boolean isVisitsDone();

    String visitDoneDateWithVisitName();

    AlertDTO getAlert(ANCServiceType serviceType);

    String pncComplications();
}

