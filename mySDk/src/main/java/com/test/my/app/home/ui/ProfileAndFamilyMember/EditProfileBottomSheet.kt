package com.test.my.app.home.ui.ProfileAndFamilyMember

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.my.app.R
import com.test.my.app.databinding.BottomSheetEditProfileBinding
import com.test.my.app.home.common.DataHandler.ProfileImgOption
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileBottomSheet(var listener: OnOptionClickListener, var hasProfileImage: Boolean) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditProfileBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        //binding.layoutAvatar.visibility = View.GONE

        if (hasProfileImage) {
            binding.layoutViewPic.visibility = View.VISIBLE
            binding.layoutRemovePhoto.visibility = View.VISIBLE
        } else {
            binding.layoutViewPic.visibility = View.GONE
            binding.layoutRemovePhoto.visibility = View.GONE
        }

    }

    private fun setClickable() {

        binding.layoutViewPic.setOnClickListener {
            dismiss()
            listener.onOptionClick(ProfileImgOption.View)
        }

        binding.layoutAvatar.setOnClickListener {
            dismiss()
            listener.onOptionClick(ProfileImgOption.Avatar)
        }

        binding.layoutOpenGallery.setOnClickListener {
            dismiss()
            listener.onOptionClick(ProfileImgOption.Gallery)
        }

        binding.layoutTakePhoto.setOnClickListener {
            dismiss()
            listener.onOptionClick(ProfileImgOption.Photo)
        }

        binding.layoutRemovePhoto.setOnClickListener {
            dismiss()
            listener.onOptionClick(ProfileImgOption.Remove)
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState);
        return BottomSheetDialog(requireContext(), theme)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }


    interface OnOptionClickListener {
        fun onOptionClick( code: String )
    }

}