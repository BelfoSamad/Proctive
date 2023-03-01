package net.roeia.proctive.ui.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler.Callback
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CompoundButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.OnCheckedStateChangeListener
import com.yesserly.resto.models.pojo.Option
import net.roeia.proctive.R
import net.roeia.proctive.databinding.DialogDateTimePickerBinding
import net.roeia.proctive.databinding.DialogManageHabitBinding
import net.roeia.proctive.models.entities.Habit
import net.roeia.proctive.utils.adapters.CHIP
import net.roeia.proctive.utils.adapters.OptionsAdapter

class ManageHabitDialog(var habit: Habit? = null, val listener: HabitListener) : DialogFragment() {
    companion object {
        private const val TAG = "ManageHabitDialog"
    }

    interface HabitListener {

        fun onHabitAdded(habit: Habit)

    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var weekDays: List<String>? = null
    private var _binding: DialogManageHabitBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* Methods
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            //Init Binding
            _binding = DialogManageHabitBinding.inflate(inflater, null, false)

            //Init OptionsView
            binding.habitAllDays.setOnCheckedChangeListener { _, checked ->
                if (checked) binding.weekDays.visibility = GONE
                else binding.weekDays.visibility = VISIBLE
            }

            binding.weekDays.setAdapter(OptionsAdapter.Builder()
                .setType(CHIP)
                .setOptions(resources.getStringArray(R.array.week_days).map { name -> Option(name) })
                .setMultiSelect(true)
                .setOnSelectedListener(object: OptionsAdapter.OnSelectedListener {
                    override fun onMultiSelected(positions: List<Int>?, values: List<String>?) {
                        weekDays = values
                    }
                })
                .build())

            //Set Habit
            if (habit != null) {
                binding.habitName.setValue(habit?.name!!)
                habit?.weekDays?.map { name -> binding.weekDays.selectAt(resources.getStringArray(R.array.week_days).indexOf(name)) }
                binding.habitTime.hour = habit?.time?.split(":")!![0].toInt()
                binding.habitTime.minute = habit?.time?.split(":")!![1].toInt()
            }

            //Init ClickListener
            binding.doneHabit.setOnClickListener {
                if (habit == null)
                    habit = Habit()

                if(binding.habitName.isValid())
                    habit?.name = binding.habitName.getValue()
                habit?.time = "${binding.habitTime.hour}:${binding.habitTime.minute}"
                if (binding.habitAllDays.isChecked) habit?.weekDays = null
                else habit?.weekDays = weekDays
                listener.onHabitAdded(habit!!)
            }

            val dialog = builder.setView(binding.root).create()
            //dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}