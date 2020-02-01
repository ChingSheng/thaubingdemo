package scottychang.thaubing.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import scottychang.thaubing.model.CorpPenaltyRecord
import scottychang.thaubing.repository.MyCallback
import scottychang.thaubing.repository.ThauBingRepository

class DetailViewModel() : ViewModel() {
    val repository = ThauBingRepository()
    val showTaxIDMetaData = MutableLiveData<CorpPenaltyRecord>()
    val showErrorMessage = MutableLiveData<String>()

    fun queryTaxID(taxID: String) {
        repository.getMetaData(taxID, object : MyCallback<CorpPenaltyRecord> {
            override fun onFailure(exception: Exception) {
                showErrorMessage.value = exception.localizedMessage
            }

            override fun onSuccess(data: CorpPenaltyRecord) {
                showTaxIDMetaData.value = data
            }
        })
    }
}