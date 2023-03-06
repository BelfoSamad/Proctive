package net.roeia.proctive.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class DaysAxisFormatter(private val monthsList: List<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return monthsList[value.toInt()]
    }
}