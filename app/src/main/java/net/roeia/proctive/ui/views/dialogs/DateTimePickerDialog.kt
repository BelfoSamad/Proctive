package net.roeia.proctive.ui.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.roeia.proctive.R
import net.roeia.proctive.databinding.DialogDateTimePickerBinding
import net.roeia.proctive.models.enums.TodoType
import java.util.*

class DateTimePickerDialog(
    private val pageType: TodoType,
    private val due: Date? = null,
    private val listener: DateTimePickerListener
) : DialogFragment() {
    companion object {
        private const val TAG = "DateTimePickerDialog"
    }

    interface DateTimePickerListener {

        fun onDateTimePicked(due: String)

    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var _binding: DialogDateTimePickerBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* Methods
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            //Init Binding
            _binding = DialogDateTimePickerBinding.inflate(inflater, null, false)

            //Set Color
            when(pageType) {
                TodoType.Goal, TodoType.SubGoal -> {
                    binding.root.backgroundTintList = requireContext().getColorStateList(R.color.green_300)
                    binding.set.backgroundTintList = requireContext().getColorStateList(R.color.green_700)
                    binding.set.setTextColor(resources.getColor(R.color.green_300, null))
                }
                TodoType.WeeklyGoal -> {
                    binding.root.backgroundTintList = requireContext().getColorStateList(R.color.yellow_300)
                    binding.set.backgroundTintList = requireContext().getColorStateList(R.color.yellow_700)
                    binding.set.setTextColor(resources.getColor(R.color.yellow_300, null))

                }
                TodoType.Task -> {
                    binding.root.backgroundTintList = requireContext().getColorStateList(R.color.blue_300)
                    binding.set.backgroundTintList = requireContext().getColorStateList(R.color.blue_700)
                    binding.set.setTextColor(resources.getColor(R.color.blue_300, null))
                }
            }

            //Set Date
            if (due != null) {
                val cal = Calendar.getInstance()
                cal.time = due
                binding.date.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                binding.time.hour = cal.get(Calendar.HOUR)
                binding.time.minute = cal.get(Calendar.MINUTE)
            }

            //Init ClickListener
            binding.set.setOnClickListener {
                listener.onDateTimePicked(
                    "${binding.date.dayOfMonth}/${binding.date.month}/${binding.date.year}," +
                            " ${binding.time.hour}:${binding.time.minute}"
                )
            }

            val dialog = builder.setView(binding.root).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}