package com.test.my.app.records_tracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.*
import com.test.my.app.common.utils.PermissionUtil.AppPermissionListener
import com.test.my.app.databinding.FragmentUploadRecordBinding
import com.test.my.app.model.entity.RecordInSession
import com.test.my.app.records_tracker.adapter.UploadRecordAdapter
import com.test.my.app.records_tracker.viewmodel.HealthRecordsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class UploadRecordFragment : BaseFragment() {

    private val viewModel: HealthRecordsViewModel by lazy {
        ViewModelProvider(this)[HealthRecordsViewModel::class.java]
    }
    private lateinit var binding: FragmentUploadRecordBinding

    private var code = ""
    private var uploadRecordAdapter: UploadRecordAdapter? = null
    private val permissionUtil = PermissionUtil
    private val fileUtils = FileUtils

    //var mimeTypes = arrayOf("image/*", "application/*|text/*")
    private var mimeTypes = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
        "application/doc",
        "text/*"
    )

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the photo picker.
        if (uri != null) {
            //imageView.setImageURI(uri)
            processPhotoPickerFile(uri)
        } else {
            Utilities.printLogError("PhotoPicker : No media selected")
        }
    }

    private val filePickerLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        Utilities.printData("result",result)
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                //handleSelectedFile(uri)
                processFilePickerFile(uri)
            }
        } else {
            Utilities.printLogError("FilePickerLauncher : No file selected")
        }
    }

    private val takePictureLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        Utilities.printData("result",result)
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
                processTakePictureFile(result.data!!.extras!!)
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackBtnClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadRecordBinding.inflate(inflater, container, false)

        requireArguments().let {
            code = it.getString(Constants.CODE, "")!!
            Utilities.printLogError("code----->$code")
        }
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        //binding.txtDocType.text = DataHandler(requireContext()).getCategoryByCode(code)
        binding.rvUploadRecords.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(context, 3)

        uploadRecordAdapter = UploadRecordAdapter(this, requireContext(), viewModel)
        binding.rvUploadRecords.adapter = uploadRecordAdapter

        viewModel.getRecordsInSession()
        //populateList()
        //binding.btnNextUploadRecord.isEnabled = false


        viewModel.recordsInSessionList.observe(viewLifecycleOwner) {
            it?.let {
                uploadRecordAdapter!!.fragment.populateList(it.toMutableList())
            }
        }
    }

    private fun setClickable() {

        binding.layoutCamera.setOnClickListener {
            proceedWithCameraPermission()
        }

        binding.layoutFile.setOnClickListener {
            showFileChooser()
        }

        binding.layoutGallery.setOnClickListener {
            //showImageChooser()
            openPhotoPicker()
        }

        binding.btnNextUploadRecord.setOnClickListener {
            val finalUploadList = uploadRecordAdapter!!.uploadRecordList
            Utilities.printData("finalUploadList", finalUploadList)
            if (finalUploadList.size > 0) {
                val bundle = Bundle()
                bundle.putString("code", code)
                it.findNavController()
                    .navigate(R.id.action_uploadRecordFragment_to_selectRelationFragment, bundle)
            } else {
                Utilities.toastMessageShort(requireContext(), resources.getString(R.string.SELECT_RECORD_TO_UPLOAD))
            }
        }

    }

    private fun addRecordToList(item: RecordInSession) {
        val list = uploadRecordAdapter!!.uploadRecordList
        uploadRecordAdapter!!.insertItem(item, list.size)
        viewModel.saveRecordsInSession(item)
        setListVisibility(true)
    }

    fun populateList(list: MutableList<RecordInSession>) {
        if (list.size > 0) {
            uploadRecordAdapter!!.updateList(list)
            setListVisibility(true)
        } else {
            setListVisibility(false)
        }
    }

    fun setListVisibility(needToShow: Boolean) {
        if (needToShow) {
            binding.rvUploadRecords.visibility = View.VISIBLE
            binding.layoutNoData.visibility = View.GONE
            // textViewNoUploadURSHP.setVisibility(View.GONE);
            //binding.btnNextUploadRecord.isEnabled = true

        } else {
            binding.rvUploadRecords.visibility = View.GONE
            binding.layoutNoData.visibility = View.VISIBLE
            //textViewNoUploadURSHP.setVisibility(View.VISIBLE);
            //binding.btnNextUploadRecord.isEnabled = false
        }
    }

    private fun proceedWithCameraPermission() {
        val permissionResult: Boolean =
            permissionUtil.checkCameraPermission(object : AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        dispatchTakePictureIntent()
                    }
                }
            }, requireContext())
        if (permissionResult) {
            dispatchTakePictureIntent()
        }
    }

    private fun showFileChooser() {
        val chooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooserIntent.type = "*/*"
        chooserIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        chooserIntent.addCategory(Intent.CATEGORY_OPENABLE)
        filePickerLauncher.launch(chooserIntent)
    }

    private fun openPhotoPicker() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
/*        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(takePictureIntent, Constants.CAMERA_SELECT_CODE)
        }*/
        takePictureLauncher.launch(takePictureIntent)
    }

//    private fun showFileChooser() {
//        val chooserIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        chooserIntent.type = "*/*"
//        chooserIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//        chooserIntent.addCategory(Intent.CATEGORY_OPENABLE)
//        startActivityForResult(Intent.createChooser(chooserIntent, resources.getString(R.string.SELECT_A_FILE)), Constants.FILE_SELECT_CODE)
//    }
//
//    private fun showImageChooser() {
//        val pickIntent = Intent(Intent.ACTION_PICK)
//        pickIntent.type = "image/*"
//        //val chooserIntent = Intent.createChooser(pickIntent,resources.getString(R.string.SELECT_PROFILE_PHOTO))
//        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
//        startActivityForResult(pickIntent, Constants.GALLERY_SELECT_CODE)
//    }

    private fun createRecordInSession(
        originalFileName: String,
        name: String,
        path: String,
        imageType: String,
        recordUri: Uri): RecordInSession {
        val id = (0..100000).random().toString()
        return RecordInSession(
            Name = name,
            Id = id,
            OriginalFileName = originalFileName,
            Path = path,
            Type = imageType,
            FileUri = recordUri.toString(),
            Sync = "")
    }

/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            Utilities.printLogError("requestCode-> $requestCode")
            Utilities.printLogError("resultCode-> $resultCode")
            Utilities.printLogError("data-> $data")
            if (resultCode == Activity.RESULT_OK && data != null) {
                when (requestCode) {
                    Constants.CAMERA_SELECT_CODE -> {
                        processTakePictureFile(data.extras!!)
                    }
                    Constants.FILE_SELECT_CODE -> {
                        processFilePickerFile(data.data!!)
                    }
                    Constants.GALLERY_SELECT_CODE -> {
                        processPhotoPickerFile(data.data!!)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun processFilePickerFile(uri:Uri) {
        try {
            //val uriFile = uri
            Utilities.printLogError("uri--->$uri")
            val filePath = fileUtils.getFilePath(requireContext(),uri)!!
            Utilities.printLogError("FilePickerLauncher : filePath: $filePath")
            val fileSize = fileUtils.calculateFileSize(filePath, "MB")
            if (fileSize <= 5.0) {
                val extension = fileUtils.getFileExt(filePath)
                Utilities.printLogError("Extension : $extension")
                if (Utilities.isAcceptableDocumentType(extension)) {
                    val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", filePath)
                    Utilities.printLogError("File Path---> $filePath")
                    val saveFile = fileUtils.saveRecordToExternalStorage(requireContext(),filePath,uri,fileName)
                    val mainDirectoryPath =
                        Utilities.getAppFolderLocation(requireContext())
                    if (saveFile != null) {
                        val origFileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                        val rsData = createRecordInSession(
                            origFileName,
                            fileName,
                            mainDirectoryPath,
                            Utilities.getDocumentTypeFromExt(extension),
                            Uri.fromFile(saveFile))
                        addRecordToList(rsData)
                    }
                } else {
                    Utilities.toastMessageLong(requireContext(),extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                }
            } else {
                Utilities.toastMessageLong(requireContext(),resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
            }
        } catch (e: Exception) {
            Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
            e.printStackTrace()
        }
    }

    private fun processPhotoPickerFile(uri:Uri) {
        try {
            Utilities.printLogError("PhotoPicker : Selected URI: $uri")
            val imagePath = fileUtils.getFilePath(requireContext(),uri)!!
            Utilities.printLogError("PhotoPicker : imagePath: $imagePath")
            val fileSize = fileUtils.calculateFileSize(imagePath, "MB")

            if (fileSize <= 5.0) {
                val extension1 = fileUtils.getFileExt(imagePath)
                Utilities.printLogError("Extension : $extension1")
                if (Utilities.isAcceptableDocumentType(extension1)) {
                    val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", imagePath)
                    Utilities.printLogError("File Path---> $imagePath")
                    val saveImage = fileUtils.saveRecordToExternalStorage(requireContext(), imagePath, uri, fileName)
                    val mainDirectoryPath = Utilities.getAppFolderLocation(requireContext())
                    if (saveImage != null) {
                        val origFileName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
                        val rsData = createRecordInSession(
                            origFileName,
                            fileName,
                            mainDirectoryPath,
                            Utilities.getDocumentTypeFromExt(extension1),
                            Uri.fromFile(saveImage))
                        addRecordToList(rsData)
                    }
                } else {
                    Utilities.toastMessageLong(context, extension1 + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED))
                }
            } else {
                Utilities.toastMessageLong(context, resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
            }
        } catch (e: Exception) {
            Utilities.toastMessageShort(context, resources.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
            e.printStackTrace()
        }
    }

    private fun processTakePictureFile(extras:Bundle) {
        try {
            val photo = extras.get("data") as Bitmap
            val documentType = "IMAGE"
            val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_REC", ".png")
            val recordFile = fileUtils.saveBitmapToExternalStorage(requireContext(),photo,fileName)
            val mainDirectoryPath = Utilities.getAppFolderLocation(requireContext())
            if (recordFile != null) {
                val fileSize = fileUtils.calculateFileSize(recordFile.toString(), "MB")
                if (fileSize <= 5.0) {
                    val rsData = createRecordInSession(fileName, fileName, mainDirectoryPath, documentType, Uri.fromFile(recordFile))
                    addRecordToList(rsData)
                } else {
                    Utilities.deleteFile(recordFile)
                    Utilities.toastMessageLong(requireContext(), resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun performBackBtnClick() {
        viewModel.deleteRecordsInSessionTable()
        findNavController().navigate(R.id.action_uploadRecordFragment_to_documentTypeFragment)
    }

}