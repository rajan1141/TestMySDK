package com.test.my.app.security.view

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.test.my.app.common.utils.Utilities
import com.test.my.app.repository.utils.Resource

object TermsAndConditionBinding {

    @BindingAdapter("app:showLoadingImage")
    @JvmStatic
    fun <T> showLoadingImage(view: AppCompatImageView, resource: Resource<T>?) {
        Utilities.printLog("Resource: $resource")
        if (resource != null) {
            if (resource.status == Resource.Status.LOADING)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }
    }

    @BindingAdapter("app:showWhenLoading")
    @JvmStatic
    fun showWhenLoading(view: View, status: Resource.Status?) {

        if (status != null) {
            if (status == Resource.Status.LOADING)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }
    }
}