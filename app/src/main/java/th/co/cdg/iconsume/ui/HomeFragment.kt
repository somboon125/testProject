package th.co.cdg.iconsume.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_home.*
import th.co.cdg.iconsume.R
import th.co.cdg.iconsume.database.DatabaseManager
import th.co.cdg.iconsume.model.ProductOutput
import th.co.cdg.iconsume.model.SearchData
import th.co.cdg.iconsume.services.ServiceManager
import th.co.cdg.iconsume.util.SwipeToDeleteCallback
import th.co.cdg.iconsume.util.hideKeyboard
import th.co.cdg.iconsume.util.hideLoading
import th.co.cdg.iconsume.util.showLoading
import java.io.File

class HomeFragment : Fragment(), View.OnClickListener, HistoryAdapter.HistoryOnClickListener {
    companion object {
        const val TAG = "HOME"
        private const val SELECT_PHOTO_REQUEST_CODE = 100
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val CAPTURE_IMAGE_FILE_PROVIDER = "th.co.cdg.iconsume.fileprovider"
    }

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val resultList = mutableListOf<SearchData>()
    private lateinit var mAdapter: HistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        currentResult?.let { tvResult.text = it.productha }
        setUpBottomSheet()
        initEvent()
        mAdapter = HistoryAdapter(requireContext(), resultList)
        mAdapter.callback = this
        rvHistory.apply {
            adapter = mAdapter
            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(mAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }
        initDatabase()
    }

    private fun setUpBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        BottomSheetBehavior.from(bottomSheetView.parent as View)
        val btnCamera = bottomSheetView.findViewById<TextView>(R.id.menu_bottom_sheet_camera)
        val btnGallery = bottomSheetView.findViewById<TextView>(R.id.menu_bottom_sheet_gallery)
        val btnCancel = bottomSheetView.findViewById<TextView>(R.id.menu_bottom_sheet_cancel)

        btnCamera.setOnClickListener {
            bottomSheetDialog.hide()
            Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        openCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) = Unit

                })
                .check()
        }

        btnGallery.setOnClickListener {
            bottomSheetDialog.hide()
            Dexter.withActivity(activity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        openGallery()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) = Unit

                })
                .check()
        }

        btnCancel.setOnClickListener {
            bottomSheetDialog.hide()
        }
    }

    private fun initEvent() {
        ivPhoto.setOnClickListener(this)
        etvNo.setOnTouchListener { v, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (v.right - etvNo.compoundDrawables[drawableRight].bounds.width())) {
                    search()
                    hideKeyboard()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }
    }

    @SuppressLint("CheckResult")
    private fun initDatabase() {
        showLoading()
        DatabaseManager.getInstance(requireContext())
            .getHistories()
            .doFinally { hideLoading() }
            .subscribe({
                resultList.clear()
                resultList.addAll(it)
                mAdapter.notifyDataSetChanged()
            }, {
                it
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PHOTO_REQUEST_CODE && data != null && data.data != null) {
                val uri = data.data!!
                startCrop(uri)
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val path = File(requireContext().filesDir, "iConsume/images/")
                if (!path.exists()) path.mkdirs()
                val image = File(path, "image.jpg")
                startCrop(image.toUri())
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (data != null) {
                    val uri = CropImage.getActivityResult(data).uri
                    uri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                        ivPhoto.setImageBitmap(bitmap)
                        runTextRecognition()
                    }
                }
            }
        }
    }

    private fun startCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivPhoto -> {
                bottomSheetDialog.show()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun search() {
        val text = etvNo.text?.toString() ?: ""
        if (text.isEmpty()) return
        showLoading()
        ServiceManager.checkOryor(text)
            .doFinally {
                hideLoading()
                mAdapter.notifyDataSetChanged()
                DatabaseManager.getInstance(requireContext())
                    .insertHistories(resultList[0])
                    .doFinally {
                        if (resultList.size > 20) {
                            val dataArray = resultList.subList(20, resultList.size).toTypedArray()
                            resultList.removeAll(dataArray)
                            DatabaseManager.getInstance(requireContext())
                                .deleteHistories(*dataArray)
                                .subscribe({
                                    mAdapter.notifyDataSetChanged()
                                    rvHistory.smoothScrollToPosition(0)
                                },{
                                    it
                                })
                        }
                    }
                    .subscribe()
            }
            .subscribe({ response ->
                if (response["output"].isJsonArray) {
                    val output = Gson().fromJson(response["output"].asJsonArray, Array<ProductOutput>::class.java).toList()
                    if (output.isNotEmpty()) {
                        val data = SearchData(text, output[0], true)
                        resultList.add(0, data)
                    } else {
                        resultList.add(0, SearchData(text))
                    }
                } else {
                    resultList.add(0, SearchData(text))
                }
            }, {
                resultList.add(0, SearchData(text))
            })
    }

    override fun onHistoryClick(data: SearchData) {
        hideKeyboard()
        val bundle = Bundle()
        bundle.putString(DetailFragment.TEXT, data.numberSearch)
        bundle.putString(DetailFragment.DATA, Gson().toJson(data.data))
        findNavController().navigate(R.id.action_nav_home_to_nav_detail, bundle)
    }

    override fun onHistoryRemove(data: SearchData) {
        showLoading()
        DatabaseManager.getInstance(requireContext())
            .deleteHistories(data)
            .doFinally { hideLoading() }
            .subscribe()
    }

    private fun checkRegEx(str: String): String {
        var txt = ""
        val listStrRegEx = listOf(
            "[0-9]{2}-[0-9]-[0-9]{5}-[0-9]-[0-9]{4}",
            "[0-9]{0,1}[ABCDEFGHKLMN][0-9]{1,4}/[0-9]{2}",
            "[0-9]{2}-[0-9]-[0-9]{10}|[0-9]{2}-[0-9]-[0-9]{7}"
        )
        listStrRegEx.forEach { strRegEx ->
            val pattern = strRegEx.toRegex()
            val matches = pattern.findAll(str, 0).map { it.value }.toList()
            if (matches.isNotEmpty()) {
                if (pattern.matches(matches[0])) {
                    txt = matches[0]
                    return txt
                }
            }
        }
        return txt
    }

    private fun openCamera() {
        val path = File(requireContext().filesDir, "iConsume/images/")
        if (!path.exists()) path.mkdirs()
        val image = File(path, "image.jpg")
        val imageUri = FileProvider.getUriForFile(requireContext(), CAPTURE_IMAGE_FILE_PROVIDER, image)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_REQUEST_CODE)
    }

    private fun runTextRecognition() {
        val bitmap = (ivPhoto.drawable as BitmapDrawable).bitmap

        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
        recognizer.processImage(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener {
                Toast.makeText(context, R.string.error_text_recognition, Toast.LENGTH_SHORT).show()
            }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        val blocks = texts.textBlocks
        if (blocks.size == 0) {
            Toast.makeText(context, R.string.error_text_recognition, Toast.LENGTH_SHORT).show()
            return
        }
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                val list = mutableListOf<String>()
                elements.forEach {
                    tvSuggest.text = "รหัสที่อ่านได้ ${it.text}"
                    ivCopy.visibility = View.VISIBLE
                    ivCopy.setOnClickListener { _ ->
                        etvNo.setText(it.text)
                    }
                    val checkRegex = checkRegEx(it.text)
                    if (checkRegex.isNotEmpty()) {
                        list.add(checkRegex)
                    }
                }
                if (list.isNotEmpty()) {
                    etvNo.setText(list[0])
                }
            }
        }
    }
}