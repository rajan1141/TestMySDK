package com.test.my.app.track_parameter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.view.ExpandedHeightListView
import com.test.my.app.databinding.ItemExpandableProfileBinding
import com.test.my.app.model.parameter.ParentProfileModel
import com.test.my.app.track_parameter.ui.DetailHistoryFragment
import com.test.my.app.track_parameter.util.TrackParameterHelper

class ExpandableProfileAdapter(
    private val mContext: Context,
    private val fragment: DetailHistoryFragment
) : RecyclerView.Adapter<ExpandableProfileAdapter.ExpandableProfileViewHolder>() {

    private var profilesList: ArrayList<ParentProfileModel> = arrayListOf()
    private var expandedParametersAdapter: ExpandedParametersAdapter? = null

    //private val appColorHelper = AppColorHelper.instance!!
    private val helper = TrackParameterHelper

    override fun getItemCount(): Int = profilesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableProfileViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expandable_profile, parent, false)
        return ExpandableProfileViewHolder(v)
    }

    override fun onBindViewHolder(holder: ExpandableProfileViewHolder, position: Int) {
        try {
            val profile: ParentProfileModel = profilesList[position]

            holder.imgExpandableProfile.setImageResource(
                helper.getProfileImageByProfileCode(
                    profile.profileCode,
                    profile.profileCode
                )
            )
            holder.txtExpandableProfile.text =
                mContext.resources.getString(helper.getProfileNameByProfileCode(profile.profileCode))

            val descParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            holder.childView.removeAllViews()
            val rvChildList = ExpandedHeightListView(mContext)
            expandedParametersAdapter =
                ExpandedParametersAdapter(mContext, fragment, profile.childParameterList)
            rvChildList.layoutManager = LinearLayoutManager(mContext)
            rvChildList.setExpanded(true)
            rvChildList.adapter = expandedParametersAdapter
            rvChildList.layoutParams = descParam
            holder.childView.addView(rvChildList)

            if (profile.isExpanded) {
                holder.childView.visibility = View.VISIBLE
                holder.imgExpand.setImageResource(R.drawable.ic_remove)
                //holder.layoutParent.setBackgroundColor(appColorHelper.secondaryColor())
                //ImageViewCompat.setImageTintList(holder.imgExpandableProfile, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)))
                //ImageViewCompat.setImageTintList(holder.imgExpand, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.white)))
                //holder.txtExpandableProfile.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            } else {
                holder.childView.visibility = View.GONE
                holder.imgExpand.setImageResource(R.drawable.ic_add)
                //holder.layoutParent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
                //ImageViewCompat.setImageTintList(holder.imgExpandableProfile, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorPrimary)))
                ///ImageViewCompat.setImageTintList(holder.imgExpand, ColorStateList.valueOf(appColorHelper.primaryColor()))
                //holder.txtExpandableProfile.setTextColor(ContextCompat.getColor(mContext, R.color.textViewColor))
            }

            holder.layoutParent.setOnClickListener {
                if (holder.childView.isShown) {
                    profilesList[holder.layoutPosition].isExpanded = false
                } else {
                    profilesList[holder.layoutPosition].isExpanded = true
                    changeStateOfItemsInLayout(profilesList[position], position)
                }
                notifyDataSetChanged()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun updateData(mProfilesList: MutableList<ParentProfileModel>) {
        this.profilesList.clear()
        this.profilesList.addAll(mProfilesList)
        this.notifyDataSetChanged()
    }

    private fun changeStateOfItemsInLayout(listItem: ParentProfileModel, p: Int) {
        for (x in profilesList.indices) {
            if (x == p) {
                listItem.isExpanded = true
                //Since this is the tapped item, we will skip
                //the rest of loop for this item and set it expanded
                continue
            }
            profilesList[x].isExpanded = false
        }
    }

    inner class ExpandableProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemExpandableProfileBinding.bind(view)
        var imgExpandableProfile = binding.imgExpandableProfile
        var txtExpandableProfile = binding.txtExpandableProfile
        var imgExpand = binding.imgExpand
        var layoutParent = binding.layoutParent
        var childView = binding.layoutChildView

        init {
            childView.visibility = View.GONE
        }
    }

}