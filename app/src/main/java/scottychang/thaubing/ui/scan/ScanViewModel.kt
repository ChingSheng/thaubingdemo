package scottychang.thaubing.ui.scan

import androidx.lifecycle.ViewModel
import scottychang.thaubing.model.ScannedItem
import scottychang.thaubing.util.SingleLiveEvent

class ScanViewModel : ViewModel() {
    val scanFail = SingleLiveEvent<String>()
    val scanSuccess = SingleLiveEvent<ScannedItem>()

    fun setScannedResult (scannedResult: String) {
        val scannedItem = ScannedItem(scannedResult)
        when (scannedItem.scannedType)  {
            ScannedItem.Type.UNKNOWN -> scanFail.value = scannedResult
            else -> scanSuccess.value = scannedItem
        }
    }
}
