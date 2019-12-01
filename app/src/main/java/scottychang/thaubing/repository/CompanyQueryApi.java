package scottychang.thaubing.repository;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyQueryApi {
    @GET("search")
    Call<JsonObject> getCompanyDataByName(@Query("q") String query);

    @GET("show/{taxID}")
    Call<JsonObject> getCompanyDataByID(@Path("taxID") String taxID);
}
