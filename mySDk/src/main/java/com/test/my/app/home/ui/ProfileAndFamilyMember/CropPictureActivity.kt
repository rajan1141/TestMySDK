package com.test.my.app.home.ui.ProfileAndFamilyMember

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Configuration
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.FileUtils
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityCropPictureBinding
import com.test.my.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CropPictureActivity : BaseActivity() {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityCropPictureBinding
    private val fileUtils = FileUtils

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityCropPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
        try {
            setUpToolbar()
            setClickable()
        } catch ( e : Exception ) {
            e.printStackTrace()
        }
    }

    private fun setClickable() {
        if ( intent.extras!!.containsKey(Constants.URI) ) {
            binding.cropImageView.setImageUriAsync(Uri.parse(intent.extras!!.getString(Constants.URI)!!))
        }
        binding.cropImageView.cropShape = com.canhub.cropper.CropImageView.CropShape.RECTANGLE
        binding.cropImageView.setFixedAspectRatio(false)

        binding.cropImageView.setOnCropImageCompleteListener { view, result ->
            if (result.isSuccessful) {
                val croppedBitmap = result.bitmap
                Utilities.printLogError("isSuccessful--->isSuccessful")
                //Utilities.printData("result",result)
                val fileName = fileUtils.generateUniqueFileName(Configuration.strAppIdentifier + "_PROFPIC", ".png")
                val saveImage = fileUtils.saveBitmapToExternalStorage(this,croppedBitmap,fileName)!!
                if ( saveImage.exists() ) {
                    val intent = Intent().apply {
                        putExtra(Constants.BITMAP,Uri.fromFile(saveImage).toString())
                    }
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            } else {
                val error = result.error
                Utilities.toastMessageLong(this,"Crop failed: ${error!!.message}")
            }
        }

        binding.imgRotate.setOnClickListener {
            binding.cropImageView.rotateImage(90)
        }

        binding.txtCrop.setOnClickListener {
            binding.cropImageView.croppedImageAsync()
        }

    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbarCrop)
        binding.toolbarCrop.title = null
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolbarCrop.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            appColorHelper.whiteColor,BlendModeCompat.SRC_ATOP)

        binding.toolbarCrop.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
    }

}