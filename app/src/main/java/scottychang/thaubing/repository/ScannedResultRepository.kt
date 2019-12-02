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
    private val GS1_QUERY_URL = "https://www.gs1tw.org/twct/web/codesearch_send.jsp?"
    private val GS1_QUERY_KEY = "MCANNO"
    private val COMPANY_QUERY_API_URL = "https://company.g0v.ronny.tw/api/"

    private lateinit var apiService: CompanyQueryApi
    private val handler = Handler(Looper.getMainLooper())

    interface ScannedResultCallback {
        fun onComplete(item: ScannedItem)
    }

    fun getScannedResult(rawString: String, callback: ScannedResultCallback) {
        val item = ScannedItem(rawString)
        if (item.scannedType == ScannedItem.Type.GS1_BAR) {
            updateMetadata(item, callback)
        } else if (item.scannedType == ScannedItem.Type.RECEIPT_QR || item.scannedType == ScannedItem.Type.TAX_ID) {
            getNameByTaxID(item.taxID, item, callback)
        } else {
            callback.onComplete(item)
        }
    }

    private fun updateMetadata(item: ScannedItem, callback: ScannedResultCallback) {
        Thread(Runnable {
            Jsoup.connect(GS1_QUERY_URL).data(GS1_QUERY_KEY, item.rawString).execute()?.let {
                it.parse()?.let {
                    val queryResult = it.body().getElementsByClass("col-md-10").first()!!
                    val queryItems = queryResult.getElementsByTag("p")
                    val name = if (queryItems.isNotEmpty()) queryItems.get(0).text().substring(7) else ""
                    Log.d(TAG, "name is: " + name)
                    item.metaData = name

                    if (name.isNotEmpty()) {
                        getTaxIDByName(name, item, callback)
                    } else {
                        handler.post { callback.onComplete(item) }
                    }
                }
            }
        }).start()
    }

    @WorkerThread
    private fun getTaxIDByName(
        name: String,
        item: ScannedItem,
        callback: ScannedResultCallback
    ) {
        val queryResult = apiService.getCompanyDataByName(name).execute()
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

    private fun getNameByTaxID(
        taxID: String,
        item: ScannedItem,
        callback: ScannedResultCallback
    ) {
        Thread(Runnable {
            val queryResult = apiService.getCompanyDataByID(taxID).execute()
            if (queryResult.isSuccessful) {
                queryResult.body()?.let {
                    val company = it.getAsJsonObject("data")
                    if (company != null) {
                        val detailData = company.get("財政部") as JsonObject
                        if (detailData.has("營業人名稱")) {
                            item.metaData = detailData.get("營業人名稱").asString
                        }
                        if (detailData.has("總機構統一編號")
                            && detailData.get("總機構統一編號").asString?.length == 8) {   // This data may not exist from api
                            item.taxID = detailData.get("總機構統一編號").asString
                        }
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
        }).start()
    }

    init {
        initApiServer()
    }

    private fun initApiServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl(COMPANY_QUERY_API_URL)
            .client(getApiClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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