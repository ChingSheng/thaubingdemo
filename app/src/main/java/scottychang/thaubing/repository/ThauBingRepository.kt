package scottychang.thaubing.repository

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import scottychang.thaubing.model.CorpPenaltyRecord


class ThauBingRepository() {
    private val API_URL = "https://thaubing.gcaa.org.tw/json/corp/"
    private lateinit var apiService: ThauBingApi
    private val gson = GsonBuilder().setLenient().create()

    fun getMetaData(taxID: String, callback : MyCallback<CorpPenaltyRecord>) {
        val result = apiService.getMetaDataByTaxID(taxID)
        result.enqueue(object: retrofit2.Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                callback.onFailure(Exception(t))
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                // This is because this string is not well-formed json string, it has an malFormed text "<!-- {text} -->" at the end for string
                val malFormedBody = response.body()!!.toString()
                val candidateIndex = malFormedBody.indexOf("<!--")
                val wellFormedBody = malFormedBody.substring(0, if (candidateIndex >= 0) candidateIndex else malFormedBody.length)
                val record = gson.fromJson(wellFormedBody, CorpPenaltyRecord::class.java)
                callback.onSuccess(record)
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
            build()
        apiService = retrofit.create(ThauBingApi::class.java)
    }

    private fun getApiClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).retryOnConnectionFailure(true).build()
    }
}