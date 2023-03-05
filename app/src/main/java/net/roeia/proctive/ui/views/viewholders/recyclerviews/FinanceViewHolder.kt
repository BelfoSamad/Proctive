package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import net.roeia.proctive.databinding.RecyclerviewFinanceItemBinding
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.models.pojo.Finance
import net.roeia.proctive.utils.BaseViewHolder
import net.roeia.proctive.utils.BasicRecyclerViewAdapter

class FinanceViewHolder constructor(private val binding: RecyclerviewFinanceItemBinding) :
    BaseViewHolder<Finance>(binding) {

    override fun onBindViewHolder(
        model: Finance?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.finance = model
        binding.type = FinanceType.fromInt(bundle.getInt("PageType"))

        val constraints = ConstraintSet()
        constraints.clone(binding.root as ConstraintLayout)
        if (model?.subDetails == null)
            constraints.connect(binding.financeDetails.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        if (model?.subName == null)
            constraints.connect(binding.financeName.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraints.applyTo(binding.root as ConstraintLayout)
    }

}