package net.roeia.proctive.ui.views.viewholders.dialogs

import android.os.Bundle
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.DialogDateTimePickerBinding
import net.roeia.proctive.models.enums.TodoType
import java.util.*

class DateTimePickerViewHolder constructor(val binding: DialogDateTimePickerBinding) :
    BaseViewHolder<Date>(binding) {

    interface DateTimePickerListener : BaseListener {

        fun onDateTimePicked(due: String)

    }

    override fun onBindViewHolder(
        model: Date?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.type = TodoType.fromInt(bundle.getInt("PAGE_TYPE"))

        //Set Date
        if (model != null) {
            val cal = Calendar.getInstance()
            cal.time = model
            binding.date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            binding.time.hour = cal.get(Calendar.HOUR)
            binding.time.minute = cal.get(Calendar.MINUTE)
        }

        //Init ClickListener
        binding.set.setOnClickListener {
            (listener as DateTimePickerListener).onDateTimePicked(
                "${binding.date.dayOfMonth}/${binding.date.month}/${binding.date.year}," +
                        " ${binding.time.hour}:${binding.time.minute}"
            )
        }
    }
}