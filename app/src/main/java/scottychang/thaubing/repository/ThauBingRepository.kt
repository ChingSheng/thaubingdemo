package scottychang.thaubing.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class ThauBingRepository() {
    private val API_URL = "https://thaubing.gcaa.org.tw/json/corp/"
    private lateinit var apiService: ThauBingApi

    fun getMetaData(taxID: String, callback : MyCallback<String>) {
        val dada = apiService.getMetaDataByTaxID(taxID)
        dada.enqueue(object: retrofit2.Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.onFailure(Exception(t))
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                callback.onSuccess(response.body() ?: "")
            }
        })
    }

    init {
        initApiServer()
    }

    private fun initApiServer() {
        val retrofit = Retrofit.Builder().
            baseUrl(API_URL).
            client(getApiClient()).
            addConverterFactory(ScalarsConverterFactory.create()).
            build();
        apiService = retrofit.create(ThauBingApi::class.java)
    }

    private fun getApiClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).retryOnConnectionFailure(true).build()
    }
}