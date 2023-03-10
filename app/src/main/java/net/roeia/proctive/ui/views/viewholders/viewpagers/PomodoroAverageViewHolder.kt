package net.roeia.proctive.ui.views.viewholders.viewpagers

import android.content.Context
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.PagePomodoroAverageBinding
import net.roeia.proctive.utils.DaysAxisFormatter
import java.util.ArrayList

class PomodoroAverageViewHolder constructor(private val binding: PagePomodoroAverageBinding) :
    BaseViewHolder<Any>(binding) {

    private var context: Context? = null

    override fun onBindViewHolder(
        model: Any?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        context = binding.root.context

        binding.averagePomodoros.text = "12 Pomodoro"
        binding.bestPomodoros.text = "12 Pomodoro"
        binding.backWeek.setOnClickListener {
            //TODO: Back Week
        }
        binding.forwardWeek.setOnClickListener {
            //TODO: Forward Week
        }

        //Pomodoro Tracking
        initMeasurementGraphStyling(binding.average)
        val entries = listOf(
            BarEntry(0f, 40f),
            BarEntry(1f, 50f),
            BarEntry(2f, 60f),
            BarEntry(3f, 20f),
            BarEntry(4f, 30f),
        )
        if (binding.average.data != null && binding.average.data.dataSetCount > 0) {
            (binding.average.data.getDataSetByIndex(0) as LineDataSet).values = entries
            binding.average.data.notifyDataChanged()
            binding.average.notifyDataSetChanged()
        } else {
            val set = BarDataSet(entries, "")
            initGraphSetStyling(set)
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set)
            val data = BarData(dataSets)
            binding.average.data = data
        }
    }

    private fun initMeasurementGraphStyling(barChart: BarChart) {
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.extraLeftOffset = 4F
        barChart.xAxis.valueFormatter = DaysAxisFormatter(context?.resources!!.getStringArray(R.array.months).toList())
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.textColor = context?.resources!!.getColor(R.color.black, null)
        barChart.axisLeft.textColor = context?.resources!!.getColor(R.color.black, null)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.typeface = ResourcesCompat.getFont(context!!, R.font.lufga_medium)
        barChart.axisLeft.typeface = ResourcesCompat.getFont(context!!, R.font.lufga_medium)
    }

    private fun initGraphSetStyling(set: BarDataSet) {
        set.color = context?.resources!!.getColor(R.color.black, null)
        set.valueTextColor = context?.resources!!.getColor(R.color.black, null)
        set.setDrawValues(false)
    }
}