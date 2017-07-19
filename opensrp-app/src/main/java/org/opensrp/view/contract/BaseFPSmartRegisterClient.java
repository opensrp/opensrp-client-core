package org.opensrp.view.contract;

import org.opensrp.domain.FPMethod;
import org.opensrp.util.IntegerUtil;

import java.util.Comparator;

public interface BaseFPSmartRegisterClient extends SmartRegisterClient {

    public FPMethod fpMethod();

    public String familyPlanningMethodChangeDate();

    public String numberOfOCPDelivered();

    public String numberOfCondomsSupplied();

    public String numberOfCentchromanPillsDelivered();

    public String iudPerson();

    public String iudPlace();

}
