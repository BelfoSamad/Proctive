package net.roeia.proctive.ui.views.viewholders.dialogs

import android.os.Bundle
import net.roeia.proctive.databinding.DialogManageExpenditureBinding
import net.roeia.proctive.models.entities.Expenditure
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.utils.BaseViewHolder
import java.util.*

class ManageExpenditureViewHolder constructor(val binding: DialogManageExpenditureBinding) :
    BaseViewHolder<Expenditure>(binding) {

    interface ExpenditureListener : BaseListener {

        fun onExpenditureSaved(expenditure: Expenditure)

    }

    override fun onBindViewHolder(
        model: Expenditure?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.type = FinanceType.fromInt(bundle.getInt("PageType"))

        //ClickListener
        binding.doneExpenditure.setOnClickListener {
            if (binding.expenditureName.isValid() && binding.expenditureAmount.isValid()) {
                (listener as ExpenditureListener).onExpenditureSaved(
                    Expenditure(
                        name = binding.expenditureName.getValue(),
                        amount = binding.expenditureAmount.getValue().toDouble(),
                        addedOn = Date()
                    )
                )
            }
        }

    }
}