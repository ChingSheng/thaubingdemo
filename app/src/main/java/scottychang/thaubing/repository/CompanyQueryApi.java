package scottychang.thaubing.repository;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CompanyQueryApi {
    @GET("search")
    Call<JsonObject> getCompanyData(@Query("q") String query);
}
