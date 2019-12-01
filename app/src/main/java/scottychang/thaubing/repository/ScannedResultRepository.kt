package scottychang.thaubing.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import scottychang.thaubing.model.ScannedItem

class ScannedResultRepository {
    private val TAG = this.javaClass.simpleName
    private val GS1_QUERY_URL = "http://www.gs1tw.org/twct/web/codesearch_send.jsp?"
    private val GS1_QUERY_KEY = "MCANNO"
    private val COMPANY_QUERY_API_URL = "http://company.g0v.ronny.tw/api/"

    private lateinit var apiService: CompanyQueryApi
    private val handler = Handler(Looper.getMainLooper())

    interface ScannedResultCallback {
        fun onComplete(item: ScannedItem)
    }

    fun getScannedResult(rawString: String, callback: ScannedResultCallback) {
        val item = ScannedItem(rawString)
        if (item.scannedType == ScannedItem.Type.GS1_BAR) {
            updateGS1Metadata(item, callback)
        } else {
            callback.onComplete(item)
        }
    }

    private fun updateGS1Metadata(item: ScannedItem, callback: ScannedResultCallback) {
        Thread(Runnable {
            Jsoup.connect(GS1_QUERY_URL).data(GS1_QUERY_KEY, item.rawString).execute()?.let {
                it.parse()?.let {
                    val queryResult = it.body().getElementsByClass("col-md-10").first()!!
                    val queryItems = queryResult.getElementsByTag("p")
                    val name = if (queryItems.isNotEmpty()) queryItems.get(0).text().substring(7) else ""
                    Log.d(TAG, "name is: " + name)
                    item.metaData = name

                    if (name.isNotEmpty()) {
                        getTaxIDIfPossible(name, callback, item)
                    } else {
                        handler.post { callback.onComplete(item) }
                    }
                }
            }
        }).start()
    }

    @WorkerThread
    private fun getTaxIDIfPossible(
        name: String,
        callback: ScannedResultCallback,
        item: ScannedItem
    ) {
        val queryResult = apiService.getCompanyData(name).execute()
        if (queryResult.isSuccessful) {
            queryResult.body()?.let {
                val companyArray = it.getAsJsonArray("data")
                if (companyArray.size() > 0) {
                    val company = companyArray.get(0)!! as JsonObject
                    val taxID = company.get("統一編號").asString
                    item.taxID = taxID ?: ""
                    handler.post { callback.onComplete(item) }
                }
            } ?: run {
                Log.d(TAG, "No body message ?")
                handler.post { callback.onComplete(item) }
            }
        } else {
            Log.w(TAG, queryResult.code().toString() + " / " + queryResult.errorBody()?.string())
            handler.post { callback.onComplete(item) }
        }
    }

    init {
        initApiServer()
    }

    private fun initApiServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl(COMPANY_QUERY_API_URL)
            .client(getApiClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        apiService = retrofit.create(CompanyQueryApi::class.java)
    }

    private fun getApiClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }
}