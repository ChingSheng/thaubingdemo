package scottychang.thaubing.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import scottychang.thaubing.repository.MyCallback
import scottychang.thaubing.repository.ThauBingRepository

class DetailViewModel() : ViewModel() {
    val repository = ThauBingRepository()
    val showTaxIDMetaData = MutableLiveData<String>()

    fun queryTaxID(taxID: String) {
        repository.getMetaData(taxID, object : MyCallback<String> {
            override fun onFailure(exception: Exception) {
                showTaxIDMetaData.value = exception.localizedMessage
            }

            override fun onSuccess(data: String) {
                showTaxIDMetaData.value = data
            }
        })
    }
}