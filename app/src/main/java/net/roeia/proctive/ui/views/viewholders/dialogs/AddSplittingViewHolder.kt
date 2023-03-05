package net.roeia.proctive.ui.views.viewholders.dialogs

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import net.roeia.proctive.R
import net.roeia.proctive.databinding.DialogAddSplittingBinding
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.utils.BaseViewHolder

class AddSplittingViewHolder constructor(val binding: DialogAddSplittingBinding) :
    BaseViewHolder<Any>(binding) {

    var financeType: FinanceType? = null
    val financeTabs = listOf("Debt", "Expenses", "Pocket Money", "Investments")

    interface SplittingListener : BaseListener {

        fun onSaveSplitting(isPercentage: Boolean, financeType: FinanceType, amount: Double)

    }

    override fun onBindViewHolder(
        model: Any?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.isSavings = bundle.getBoolean("isSavings")

        //Init Finance Spinners
        ArrayAdapter(
            binding.root.context,
            R.layout.simple_spinner_item,
            financeTabs
        ).also { adapter ->
            binding.financeTabs.adapter = adapter
            binding.financeTabs.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        //Set Activity level on ViewModel
                        financeType = FinanceType.fromInt(
                            binding.root.context.resources.getStringArray(R.array.finance_title)
                                .indexOf(financeTabs[p2])
                        )
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //Do Nothing
                    }
                }
        }

        //Save Splitting
        binding.doneSplitting.setOnClickListener {
            if (binding.splittingAmount.isValid())
                (listener as SplittingListener).onSaveSplitting(
                    binding.percentageMode.isChecked,
                    financeType!!,
                    binding.splittingAmount.getValue().toDouble()
                )
        }
    }
}