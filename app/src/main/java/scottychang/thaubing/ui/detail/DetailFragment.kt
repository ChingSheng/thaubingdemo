package scottychang.thaubing.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.detail_fragment.*
import scottychang.thaubing.R
import scottychang.thaubing.model.ScannedItem

class DetailFragment : Fragment(), LifecycleObserver {
    companion object {
        private val EXTRA_SCANNED_ITEM = "extra_scanned_item"

        fun newInstance(scannedItem : ScannedItem): DetailFragment {
            val fragment = DetailFragment()
            val arguments = Bundle()
            arguments.putParcelable(EXTRA_SCANNED_ITEM, scannedItem)
            fragment.arguments  = arguments
            return fragment
        }
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var scannedItem: ScannedItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        lifecycle.addObserver(this)
        return  inflater.inflate(R.layout.detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scannedItem =  arguments!!.getParcelable<ScannedItem>(EXTRA_SCANNED_ITEM)!!
        type.text = "掃描種類: " + scannedItem.scannedType.name
        tax_id.text = if (scannedItem.taxID.isNotEmpty()) "統一編號: " + scannedItem.taxID + " (" + scannedItem.metaData + ")" else "未知的統一編號"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.showTaxIDMetaData.observe(this, Observer<String> { t -> detail_raw.text = t })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun setupMetaData() {
        if (scannedItem.taxID.isNotEmpty()) {
            viewModel.queryTaxID(scannedItem.taxID)
        } else {
            detail_raw.text = "掃描訊息: " + scannedItem.rawString
        }
    }
}