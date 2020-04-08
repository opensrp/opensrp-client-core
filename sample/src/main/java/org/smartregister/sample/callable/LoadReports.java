package org.smartregister.sample.callable;

import org.smartregister.sample.domain.FakeApiPayload;
import org.smartregister.sample.domain.Report;
import org.smartregister.sample.tools.RetrofitClientInstance;
import org.smartregister.sample.webservice.ReportsWebservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Reads a fake API and returns the results
 */
public class LoadReports implements Callable<List<Report>> {

    @Override
    public List<Report> call() throws Exception {
        ReportsWebservice service = RetrofitClientInstance.getRetrofitInstance().create(ReportsWebservice.class);
        Call<FakeApiPayload<List<Report>>> call = service.getEmployeesReport();
        Response<FakeApiPayload<List<Report>>> response = call.execute();
        if (response.body() != null) {
            return response.body().getData();
        }
        return new ArrayList<>();
    }

}