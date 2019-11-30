package scottychang.thaubing.repository;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ThauBingApi {
    @GET("{taxID}")
    Call<String> getMetaDataByTaxID(@Path("taxID") String taxID);
}
