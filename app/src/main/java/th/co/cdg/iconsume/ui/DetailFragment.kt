package th.co.cdg.iconsume.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_detail.*
import th.co.cdg.iconsume.R
import th.co.cdg.iconsume.model.ProductOutput

class DetailFragment : Fragment() {
    companion object {
        const val DATA = "DATA"
        const val IMAGE = "IMAGE"
        const val TEXT = "TEXT"
    }

    private var data: ProductOutput? = null
    private var imagePath: String? = null
    private var searchText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = Gson().fromJson(it.getString(DATA), ProductOutput::class.java)
            imagePath = it.getString(IMAGE)
            searchText = it.getString(TEXT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data?.let { initData(it) } ?: initNoData()

    }

    private fun initData(data: ProductOutput) {
        tvProductNameTh.text = data.productha
        tvProductNameEn.text = data.produceng
        tvStatus.text = data.cncnm
        tvCategory.text = data.typepro
        tvAuthorNo.text = data.lcnno
        tvAuthorName.text = data.licen
        tvPlace.text = data.thanm
        tvAddress.text = data.Addr
        tvNewCode.text = data.NewCode
    }

    private fun initNoData() {
        tvProductNameTh.text = "ไม่พบข้อมูล"
        tvProductNameEn.text = "ไม่พบข้อมูล"
        tvStatus.text = "ไม่พบข้อมูล"
        tvCategory.text = "ไม่พบข้อมูล"
        tvAuthorNo.text = searchText ?: ""
        tvAuthorName.text = "ไม่พบข้อมูล"
        tvPlace.text = "ไม่พบข้อมูล"
        tvAddress.text = "ไม่พบข้อมูล"
        tvNewCode.text = "ไม่พบข้อมูล"
    }
}
