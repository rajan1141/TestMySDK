package com.test.my.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemCalenderDashboardBinding
import com.test.my.app.medication_tracker.model.MedCalender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardCalenderAdapter(val context: Context,
                               val listener : OnDashboardDateListener) : RecyclerView.Adapter<DashboardCalenderAdapter.DashboardCalenderViewHolder>() {

    private var calenderDateList: MutableList<MedCalender> = mutableListOf()
    private var selectedPos = 0
    private var currentDateString = DateHelper.currentDateAsStringyyyyMMdd
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private var strDate = Date()
    private val currentDate = sdf.parse(currentDateString)

    override fun getItemCount(): Int = calenderDateList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardCalenderViewHolder {
        return DashboardCalenderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_calender_dashboard,parent, false))
    }

    @SuppressLint("SetTextI18n", "RecyclerView")
    override fun onBindViewHolder(holder: DashboardCalenderViewHolder, position: Int) {
        val date = calenderDateList[position]

        holder.txtDayName.text = Utilities.getDayOfWeekConverted(date.DayOfWeek, context)
        holder.txtDay.text = date.DayOfMonth

        /*        if (selectedPos == position) {
                    holder.layoutSelectedDay.background = ContextCompat.getDrawable(context, R.drawable.btn_fill_selected)
                    holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.white))
                    holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    holder.layoutSelectedDay.background = ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
                    holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
                }*/

        strDate = sdf.parse(date.Date)!!

        if ( strDate == currentDate || strDate!!.before(currentDate) ) {
            holder.layoutSelectedDay.isEnabled = true
            holder.layoutSelectedDay.background = ContextCompat.getDrawable(context, R.drawable.bg_transparant)
            holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.almost_black))
            holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
        }  else {
            holder.layoutSelectedDay.isEnabled = false
            holder.layoutSelectedDay.background = ContextCompat.getDrawable(context, R.drawable.bg_transparant)
            holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.vivantInactive))
            holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.vivantInactive))
        }

        if (selectedPos == position) {
            holder.layoutSelectedDay.background = ContextCompat.getDrawable(context, R.drawable.btn_fill_selected)
            holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.layoutSelectedDay.setOnClickListener {
            listener.onDashboardDateClick(date,position)
            //notifyItemChanged(selectedPos)
            selectedPos = position
            //notifyItemChanged(selectedPos)
            notifyDataSetChanged()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<MedCalender>) {
        calenderDateList.clear()
        calenderDateList.addAll(list)
        for ( i in calenderDateList ) {
            if ( currentDateString == i.Date ) {
                selectedPos = calenderDateList.indexOf(i)
                break
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedDate(date: String) {
        for ( i in calenderDateList ) {
            if ( i.Date == date ) {
                selectedPos = calenderDateList.indexOf(i)
                break
            }
        }
        notifyDataSetChanged()
        listener.onDashboardDateClick(calenderDateList[selectedPos],selectedPos)
    }

    interface OnDashboardDateListener {
        fun onDashboardDateClick(date: MedCalender, newDayPosition: Int)
    }

    inner class DashboardCalenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemCalenderDashboardBinding.bind(view)
        val txtDayName = binding.txtDayName
        val txtDay = binding.txtDay
        val layoutSelectedDay = binding.layoutSelectedDay
    }

}