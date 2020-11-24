package org.smartregister.configuration;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;

import java.util.Date;

public interface BaseMemberProfileRowsDataProvider {

    // TODO -> Get registrationDate

    Date getLastVisitDate(CommonPersonObjectClient client);

    AlertStatus getFamilyAlertStatus(CommonPersonObjectClient client);

    Alert getUpcomingServicesAlert(CommonPersonObjectClient client);

}