package net.roeia.proctive.ui.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.roeia.proctive.databinding.DialogDateTimePickerBinding
import java.util.*

class DateTimePickerDialog(
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
            //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}