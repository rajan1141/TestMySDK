package com.test.my.app.wyh.common

import com.test.my.app.model.wyh.healthContent.GetAllItemsModel.Article
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

data class MeasurementStatusResponse(
    @Expose
    @SerializedName("messageType")
    val messageType: String, // MEASUREMENT_STATUS
    @Expose
    @SerializedName("data")
    val data: MeasurementStatusData
)

data class MeasurementStatusData(
    @Expose
    @SerializedName("statusCode")
    val statusCode: String? = null,
    @Expose
    @SerializedName("statusMessage")
    val statusMessage: String? = null
)

data class FaceScanResponse(
    @Expose
    @SerializedName("messageType")
    val messageType: String, // Type of the message (e.g., MEASUREMENT_MEAN_DATA)
    @Expose
    @SerializedName("data")
    val faceScanData: FaceScanData // The actual biometric data
)

data class FaceScanData(
    @Expose
    @SerializedName("statusCode")
    val statusCode: String? = null,
    @Expose
    @SerializedName("statusMessage")
    val statusMessage: String? = null,
    @Expose
    @SerializedName("bpm")
    val bpm: Int = 0, // Heart rate in beats per minute
    @Expose
    @SerializedName("rr")
    val rr: Double = 0.0, // Respiratory rate
    @Expose
    @SerializedName("oxygen")
    val oxygen: Double = 0.0, // Oxygen saturation level
    @Expose
    @SerializedName("stressStatus")
    val stressStatus: String? = null, // Stress level status
    @Expose
    @SerializedName("bloodPressureStatus")
    val bloodPressureStatus: String? = null, // Blood pressure status
    @Expose
    @SerializedName("systolic") var
    systolic: Int = 0, // Systolic blood pressure
    @Expose
    @SerializedName("diastolic") var
    diastolic: Int = 0, // Diastolic blood pressure
    @Expose
    @SerializedName("sdnn")
    var hrv: Int = 0, // Heart rate variability
    @Expose
    @SerializedName("stress_index")
    var stressIndex: Double = 0.0, // Stress Index
)

data class FaceScanHrvResponse(
    @Expose
    @SerializedName("messageType")
    val messageType: String,
    @Expose
    @SerializedName("data")
    val data: HrvData
)

data class HrvData(
    @Expose
    @SerializedName("ibi")
    var ibi: Double = 0.0,
    @Expose
    @SerializedName("rmssd")
    var rmssd: Double = 0.0,
    @Expose
    @SerializedName("sdnn")
    var hrv: Double = 0.0,
    @Expose
    @SerializedName("stress_index")
    var stressIndex: Double = 0.0,
    @Expose
    @SerializedName("stress_label")
    val stressLabel: String? = null,
    @Expose
    @SerializedName("rr")
    var rr : List<Double>  = listOf()
)

data class Category(
    var id: Int = 0,
    var code: String = "",
    var name: String = ""
)

data class HealthContent(
    val id: Int? = 0,
    val contentImgUrl: String? = "",
    val contentName: String? = "",
    val contentDesc: String? = "",
    val contentUrl: String? = "",
    val htmlContent: String? = "",
    val isActive: Boolean? = false,
    val articleCode: String? = "",
    var isBookMarked: Boolean = false,

    val tags: String? = "",
    val productUrl: String? = "",
    val createdBy: String? = "",
    val createdOn: String? = "",
    val updatedBy: String? = "",
    val updatedOn: String? = ""
)

data class Section(
    val sectionCode: String? = "",
    val sectionList : List<Article> = mutableListOf()
)

class CustomSpeedLinearLayoutManager(context: Context,
                                     private val duration: Int) : LinearLayoutManager(context,VERTICAL,false) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                // Custom duration
                return duration / displayMetrics.densityDpi.toFloat()
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}

class FastLinearLayoutManager(context: Context,
    private val durationMillis: Int) : LinearLayoutManager(context,VERTICAL,false) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                // Faster scrolling by reducing time per pixel
                return durationMillis / displayMetrics.widthPixels.toFloat()
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }
}

fun ViewPager2.setScrollDuration(durationMillis: Int) {
    val recyclerView = this.getChildAt(0) as RecyclerView
    recyclerView.layoutManager = FastLinearLayoutManager(this.context, durationMillis)
}

/*
fun ViewPager2.setScrollDuration(duration: Int) {
    val recyclerView = this.getChildAt(0) as RecyclerView
    recyclerView.layoutManager = CustomSpeedLinearLayoutManager(this.context, duration)
}*/
