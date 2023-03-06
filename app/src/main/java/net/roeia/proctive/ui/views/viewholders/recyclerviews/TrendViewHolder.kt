package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.os.Bundle
import net.roeia.proctive.databinding.RecyclerviewTrendItemBinding
import net.roeia.proctive.models.entities.Trend
import net.roeia.proctive.base.ui.BaseViewHolder

class TrendViewHolder constructor(private val binding: RecyclerviewTrendItemBinding) :
    BaseViewHolder<Trend>(binding) {

    override fun onBindViewHolder(
        model: Trend?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        val change = if (model?.isIncreasing!!) "Increased by" else "Decreased by"
        binding.trend.text = "${model?.element} $change ${model.changePercentage}% last ${model.interval} ${model.intervalUnit}"
    }

}