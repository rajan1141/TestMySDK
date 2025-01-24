package com.test.my.app.home.views

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.home.adapter.RvpFamilyMemberListAdapter
import com.test.my.app.model.entity.UserRelatives


object ProfileBinding {

    @BindingAdapter("app:familyList")
    @JvmStatic
    fun RecyclerView.setFamilyList(list: List<UserRelatives>?) {
        with(this.adapter as RvpFamilyMemberListAdapter) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            list?.let { updateFamilyMembersList(it) }
        }
    }

}