package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.os.Bundle
import net.roeia.proctive.databinding.RecyclerviewHabitItemBinding
import net.roeia.proctive.models.entities.Habit
import net.roeia.proctive.utils.BaseViewHolder

class HabitViewHolder constructor(private val binding: RecyclerviewHabitItemBinding) :
    BaseViewHolder<Habit>(binding) {

    interface HabitClickListener : BaseListener {

        fun onHabitChecked(habit: Habit, position: Int, checked: Boolean) {}

        fun onHabitUpdate(habit: Habit, position: Int) {}

        fun onHabitDelete(habit: Habit, position: Int) {}

    }

    override fun onBindViewHolder(
        model: Habit?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.habit = model
        binding.position = position
        binding.listener = listener as HabitClickListener
        binding.isUpdate = bundle.getBoolean("isUpdate")

        //OnChecked Changed
        binding.habitCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked)
                listener.onHabitChecked(model!!, position, true)
            else listener.onHabitChecked(model!!, position, false)
        }
    }

}