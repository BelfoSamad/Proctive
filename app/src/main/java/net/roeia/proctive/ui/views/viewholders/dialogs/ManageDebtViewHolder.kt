package net.roeia.proctive.ui.views.viewholders.dialogs

import android.os.Bundle
import net.roeia.proctive.R
import net.roeia.proctive.databinding.DialogManageDebtBinding
import net.roeia.proctive.databinding.DialogManageExpenditureBinding
import net.roeia.proctive.models.entities.Debt
import net.roeia.proctive.models.entities.Expenditure
import net.roeia.proctive.utils.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class ManageDebtViewHolder constructor(val binding: DialogManageDebtBinding) :
    BaseViewHolder<Debt>(binding) {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    interface DebtListener {

        fun onSaveDebt(debtHolder: Debt)

    }

    override fun onBindViewHolder(
        model: Debt?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        //Done Debt
        binding.doneDebt.setOnClickListener {
            if (binding.debtAmount.isValid() && binding.debtorName.isValid() && binding.debtPaid.isValid()) {
                (listener as DebtListener).onSaveDebt(
                    Debt(
                        debtor = binding.debtorName.getValue(),
                        amount = binding.debtAmount.getValue().toDouble(),
                        amountPaid = binding.debtPaid.getValue().toDouble(),
                        debtReverse = binding.debtTo.isChecked,
                        returnDate = dateFormat.parse("${binding.datePicker.dayOfMonth}/${binding.datePicker.month}/${binding.datePicker.year}")
                    )
                )
            }
        }
    }
}