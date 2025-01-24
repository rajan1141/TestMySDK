package com.test.my.app.water_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.my.app.R
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ItemWeekDayBinding

class WeekDayAdapter(
    val context: Context,
    private var drinkTypeList: List<String>
) : RecyclerView.Adapter<WeekDayAdapter.MedicineTypeViewHolder>() {

    //private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = drinkTypeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_week_day, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        holder.txtWeekDay.text = Utilities.getDayOfWeekConverted(drinkTypeList[position], context)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemWeekDayBinding.bind(view)
        val txtWeekDay = binding.txtWeekDay
    }

}
