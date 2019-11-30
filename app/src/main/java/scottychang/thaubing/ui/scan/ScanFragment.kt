package scottychang.thaubing.ui.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.google.zxing.Result
import kotlinx.android.synthetic.main.scan_fragment.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import scottychang.thaubing.R
import scottychang.thaubing.model.ScannedItem
import scottychang.thaubing.ui.detail.DetailFragment

class ScanFragment : Fragment() , ZXingScannerView.ResultHandler, LifecycleObserver {
    companion object {
        fun newInstance() = ScanFragment()
    }

    private lateinit var viewModel: ScanViewModel

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startCamera() {
        scanner_view.startCamera();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseCamera() {
        scanner_view.stopCamera();
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        lifecycle.addObserver(this)
        return  inflater.inflate(R.layout.scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        viewModel.scanFail.observe(this, Observer<String>(this@ScanFragment::showToastAndResumeCamera))
        viewModel.scanSuccess.observe(this, Observer<ScannedItem>(this@ScanFragment::showScannedResultPage))
        scanner_view.setResultHandler(this)
    }

    private fun showToastAndResumeCamera(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
        scanner_view.resumeCameraPreview(this)
    }

    private fun showScannedResultPage(scannedItem: ScannedItem) {
        activity?.supportFragmentManager?.let {
            val transaction = it.beginTransaction()
            transaction.replace(R.id.container, DetailFragment.newInstance(scannedItem))
                .addToBackStack("detail")
                .commit()
        }
    }

    override fun handleResult(rawResult: Result?) {
        viewModel.setScannedResult(rawResult?.text ?: "")
    }
}
