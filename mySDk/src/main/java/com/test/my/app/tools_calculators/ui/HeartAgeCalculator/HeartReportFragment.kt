package com.test.my.app.tools_calculators.ui.HeartAgeCalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.test.my.app.R
import com.test.my.app.common.base.BaseFragment
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.FragmentHeartReportBinding
import com.test.my.app.model.toolscalculators.UserInfoModel
import com.test.my.app.tools_calculators.adapter.HeartRiskReportAdapter
import com.test.my.app.tools_calculators.adapter.ParamDataReportAdapter
import com.test.my.app.tools_calculators.model.CalculatorDataSingleton
import com.test.my.app.tools_calculators.model.HeartAgeSummeryModel
import com.test.my.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeartReportFragment : BaseFragment() {


    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentHeartReportBinding

    private var heartRiskReportAdapter: HeartRiskReportAdapter? = null
    private var paramDataReportAdapter: ParamDataReportAdapter? = null
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    Utilities.printLog("CalculatorTO=> $extra")
                    if (extra.equals("DASH", true)) {
                        findNavController().navigate(R.id.action_heartReportFragment_to_toolsCalculatorsDashboardFragment)
                    } else {
                        UserInfoModel.getInstance()!!.isDataLoaded = false
                        requireActivity().finish()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeartReportBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        initialiseChart()
        setClickable()
        return binding.root
    }

    private fun initialise() {

        heartRiskReportAdapter = HeartRiskReportAdapter(
            calculatorDataSingleton!!.heartAgeSummery.heartRiskReport,
            requireContext()
        )
        binding.heartRiskRecycler.adapter = heartRiskReportAdapter

        paramDataReportAdapter = ParamDataReportAdapter(
            calculatorDataSingleton!!.heartAgeSummery.parameterReport,
            requireContext()
        )
        binding.paramListRecyler.adapter = paramDataReportAdapter

        binding.txtRecommendationTitle.text =
            calculatorDataSingleton!!.heartAgeSummery.heartAgeReport.title

        binding.txtRecommendationDescription.text =
            calculatorDataSingleton!!.heartAgeSummery.heartAgeReport.description

        binding.txtHeartAge.text = calculatorDataSingleton!!.heartAge
        binding.txtHeartRisk.text = calculatorDataSingleton!!.riskScorePercentage
        binding.txtRisk2.text = calculatorDataSingleton!!.riskLabel

        try {
            val riskPercentage = calculatorDataSingleton!!.riskScorePercentage.toDouble()

            val color: Int = when {
                riskPercentage <= 10 -> {
                    R.color.risk_normal
                }
//                riskPercentage in 1.0..10.0 -> {
//                    R.color.vivant_marigold
//                }
                riskPercentage in 11.0..20.0 -> {
                    R.color.vivant_orange_yellow
                }

                else -> {
                    R.color.risk_severe
                }
            }
            binding.txtRisk2.setTextColor(ContextCompat.getColor(requireContext(), color))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialiseChart() {
        setHeartAgeChartData()
        setHeartRiskChartData()

        val rightAxis: YAxis = binding.barChartHeartAge.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
        val leftAxis: YAxis = binding.barChartHeartAge.axisLeft
        leftAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxis.setLabelCount(5, true)
        leftAxis.setDrawTopYLabelEntry(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.granularity = 1f
        leftAxis.axisMinimum = 0f
        binding.barChartHeartAge.setFitBars(true) // make the x-axis fit exactly all bars
        binding.barChartHeartAge.legend.isEnabled = false
        val description = Description()
        description.text = ""
        binding.barChartHeartAge.setTouchEnabled(false)
        binding.barChartHeartAge.isDoubleTapToZoomEnabled = false
        binding.barChartHeartAge.description = description
        binding.barChartHeartAge.invalidate() // refresh

        // Initialise Heart risk chart Data
        val rightAxisRisk: YAxis = binding.barChartHeartRisk.axisRight
        rightAxisRisk.setDrawGridLines(false)
        rightAxisRisk.setDrawLabels(false)
        val leftAxisRisk: YAxis = binding.barChartHeartRisk.axisLeft
        leftAxisRisk.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxisRisk.setLabelCount(5, true)
        leftAxisRisk.setDrawTopYLabelEntry(true)
        leftAxisRisk.setDrawZeroLine(true)
        leftAxisRisk.granularity = 1f
        leftAxisRisk.axisMinimum = 0f
        binding.barChartHeartRisk.setFitBars(true) // make the x-axis fit exactly all bars
        binding.barChartHeartRisk.legend.isEnabled = false
        binding.barChartHeartRisk.setTouchEnabled(false)
        binding.barChartHeartRisk.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartRisk.description = descriptionRisk
        binding.barChartHeartRisk.invalidate() // refresh
    }

    private fun setHeartAgeChartData() {
        val entries: ArrayList<BarEntry> = ArrayList()
        val personAge: Float = calculatorDataSingleton!!.personAge.toFloat()
        entries.add(BarEntry(0f, personAge))
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> =
            calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size + 1)
        colorArray[0] = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        for (i in 1..summeryModelArrayList.size) {
            val heartAge: Float = summeryModelArrayList[i - 1].heartAge.toFloat()
            entries.add(BarEntry(i.toFloat(), heartAge))
            if (personAge <= heartAge) {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.risk_severe)
            } else {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.risk_normal)
            }
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        set.valueTextSize = 10f
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartAge.data = data

        // the labels that should be drawn on the XAxis
        val xAxis: XAxis = binding.barChartHeartAge.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter =
            IAxisValueFormatter { value, _ ->
                var name = resources.getString(R.string.YOUR_AGE)
                if (value > 0) {
                    name = "${resources.getString(R.string.HEART_AGE)} " + value.toInt()
                }
                name
            }
        binding.barChartHeartAge.invalidate() // refresh
    }

    private fun setHeartRiskChartData() {
        val entries: MutableList<BarEntry> = ArrayList()
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> =
            calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size)
        for (i in summeryModelArrayList.indices) {
            val heartRisk: Float = summeryModelArrayList[i].heartRisk.toFloat()
            /*if (heartRisk < 1) {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey)
            } else if (heartRisk in 1.0..10.0) {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey)
            } else if (heartRisk > 10 && heartRisk <= 20) {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey)
            } else {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey)
            }*/
            val heartColour: Int = when {
                heartRisk <= 10 -> {
                    R.color.risk_normal
                }

                heartRisk in 11.0..20.0 -> {
                    R.color.vivant_orange_yellow
                }

                else -> {
                    R.color.risk_severe
                }
            }
            colorArray[i] = ContextCompat.getColor(requireContext(), heartColour)
            entries.add(BarEntry(i.toFloat(), heartRisk))
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        set.valueTextSize = 10f
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //        data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartRisk.data = data
        val xAxis: XAxis = binding.barChartHeartRisk.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            "${resources.getString(R.string.HEART_RISK)} " + (value.toInt() + 1)
        }
        binding.barChartHeartRisk.invalidate() // refresh
    }

    private fun setClickable() {

        binding.btnRestart.setOnClickListener {
            it.findNavController().navigate(R.id.action_heartReportFragment_to_heartAgeFragment)
        }

    }

}
