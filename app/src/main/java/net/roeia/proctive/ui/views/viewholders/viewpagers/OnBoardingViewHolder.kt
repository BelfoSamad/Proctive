package net.roeia.proctive.ui.views.viewholders.viewpagers

import android.os.Bundle
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.PageHypeBinding
import net.roeia.proctive.models.pojo.Hype

class OnBoardingViewHolder constructor(private val binding: PageHypeBinding) :
    BaseViewHolder<Hype>(binding) {

    override fun onBindViewHolder(
        model: Hype?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.hype = model
    }

}