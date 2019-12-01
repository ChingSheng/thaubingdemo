package scottychang.thaubing.ui.scan

import androidx.lifecycle.ViewModel
import scottychang.thaubing.model.ScannedItem
import scottychang.thaubing.repository.ScannedResultRepository
import scottychang.thaubing.repository.ScannedResultRepository.ScannedResultCallback
import scottychang.thaubing.util.SingleLiveEvent

class ScanViewModel : ViewModel() {
    val scanFail = SingleLiveEvent<String>()
    val scanSuccess = SingleLiveEvent<ScannedItem>()
    val repository  = ScannedResultRepository()

    fun setScannedResult (scannedResult: String) {
        repository.getScannedResult(scannedResult, object: ScannedResultCallback {
            override fun onComplete(item: ScannedItem) {
                when (item.scannedType)  {
                    ScannedItem.Type.UNKNOWN -> scanFail.value = scannedResult
                    else -> scanSuccess.value = item
                }
            }
        })
    }
}
