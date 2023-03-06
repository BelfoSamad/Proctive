package net.roeia.proctive.ui.views.viewholders.dialogs

import android.os.Bundle
import android.view.View
import com.yesserly.resto.models.pojo.Option
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.DialogManageHabitBinding
import net.roeia.proctive.models.entities.todo.Habit
import net.roeia.proctive.ui.custom.CHIP
import net.roeia.proctive.ui.custom.OptionsView

class ManageHabitViewHolder constructor(val binding: DialogManageHabitBinding) :
    BaseViewHolder<Habit>(binding) {

    //Declarations
    private var weekDays: List<String>? = null

    //Listener
    interface HabitListener : BaseListener {

        fun onHabitAdded(habit: Habit)

    }

    override fun onBindViewHolder(
        model: Habit?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        //Init OptionsView
        binding.habitAllDays.setOnCheckedChangeListener { _, checked ->
            if (checked) binding.weekDays.visibility = View.GONE
            else binding.weekDays.visibility = View.VISIBLE
        }

        binding.weekDays.setAdapter(
            OptionsView.OptionsAdapter.Builder()
            .setType(CHIP)
            .setOptions(binding.root.resources.getStringArray(R.array.week_days).map { name -> Option(name) })
            .setMultiSelect(true)
            .setOnSelectedListener(object: OptionsView.OptionsAdapter.OnSelectedListener {
                override fun onMultiSelected(positions: List<Int>?, values: List<String>?) {
                    weekDays = values
                }
            })
            .build())

        //Set Habit
        if (model != null) {
            binding.habitName.setValue(model.name!!)
            model.weekDays?.map { name -> binding.weekDays.selectAt(binding.root.resources.getStringArray(R.array.week_days).indexOf(name)) }
            binding.habitTime.hour = model.time?.split(":")!![0].toInt()
            binding.habitTime.minute = model.time?.split(":")!![1].toInt()
        }

        //Init ClickListener
        binding.doneHabit.setOnClickListener {
            var habit = model
            if (model == null)
                habit = Habit()
            if(binding.habitName.isValid()) {
                habit?.name = binding.habitName.getValue()
                habit?.time = "${binding.habitTime.hour}:${binding.habitTime.minute}"
                if (binding.habitAllDays.isChecked) habit?.weekDays = null
                else habit?.weekDays = weekDays
                (listener as HabitListener).onHabitAdded(habit!!)
            }
        }
    }
}