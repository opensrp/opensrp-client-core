package org.smartregister.view.interactor;

import org.joda.time.LocalDate;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.BaseMemberProfileRowsDataProvider;
import org.smartregister.domain.Alert;
import org.smartregister.domain.AlertStatus;
import org.smartregister.util.AppExecutors;
import org.smartregister.view.contract.ConfigurableMemberProfileActivityContract;

import java.util.Date;

public class BaseConfigurableMemberProfileInteractor implements ConfigurableMemberProfileActivityContract.Interactor {
    protected AppExecutors appExecutors;
    BaseMemberProfileRowsDataProvider dataProvider;

    BaseConfigurableMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public BaseConfigurableMemberProfileInteractor(BaseMemberProfileRowsDataProvider dataProvider) {
        this(new AppExecutors());
        this.dataProvider = dataProvider;
    }

    @Override
    public void refreshProfileView(CommonPersonObjectClient client, boolean isForEdit, ConfigurableMemberProfileActivityContract.InteractorCallBack callBack) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {

            Date lastVisitDate = getLastVisitDate(dataProvider, client);
            AlertStatus familyAlertStatus = getFamilyAlertStatus(dataProvider, client);
            Alert upcomingService = getUpcomingServicesAlert(dataProvider, client);

            callBack.refreshProfileTopSection(client);
            callBack.refreshFamilyStatus(familyAlertStatus);
            callBack.refreshLastVisit(lastVisitDate);
            if (upcomingService == null) {
                callBack.refreshUpComingServicesStatus("", AlertStatus.complete, new Date());
            } else {
                callBack.refreshUpComingServicesStatus(upcomingService.scheduleName(), upcomingService.status(), new LocalDate(upcomingService.startDate()).toDate());
            }
        });
        appExecutors.diskIO().execute(runnable);
    }

    private Date getLastVisitDate(BaseMemberProfileRowsDataProvider dataProvider, CommonPersonObjectClient client) {
        // return dataProvider.getLastVisitDate(client);
        return new Date();
    }

    private AlertStatus getFamilyAlertStatus(BaseMemberProfileRowsDataProvider dataProvider, CommonPersonObjectClient client) {
        // return dataProvider.getFamilyAlertStatus(client);
        return AlertStatus.normal;
    }

    private Alert getUpcomingServicesAlert(BaseMemberProfileRowsDataProvider dataProvider, CommonPersonObjectClient client) {
        // return dataProvider.getUpcomingServicesAlert(client);
        return null;
    }
}
