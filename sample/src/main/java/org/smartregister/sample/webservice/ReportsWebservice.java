package org.smartregister.sample.webservice;

import org.smartregister.sample.domain.FakeApiPayload;
import org.smartregister.sample.domain.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/***
 *
 */
public interface ReportsWebservice {

    @GET("employees")
    Call<FakeApiPayload<List<Report>>> getEmployeesReport();

}
