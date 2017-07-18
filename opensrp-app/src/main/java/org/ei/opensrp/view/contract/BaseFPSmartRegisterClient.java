package org.ei.opensrp.view.contract;

import org.ei.opensrp.domain.FPMethod;
import org.ei.opensrp.util.IntegerUtil;

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
