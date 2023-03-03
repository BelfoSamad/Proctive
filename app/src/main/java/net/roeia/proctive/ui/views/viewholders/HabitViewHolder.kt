package net.roeia.proctive.ui.views.viewholders

import android.os.Bundle
import net.roeia.proctive.databinding.RecyclerviewHabitItemBinding
import net.roeia.proctive.models.entities.Habit
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter

class HabitViewHolder constructor(private val b: RecyclerviewHabitItemBinding) :
    BasicRecyclerViewAdapter.BaseViewHolder<Habit>(b) {

    interface HabitClickListener : BasicRecyclerViewAdapter.BaseListener {

        fun onHabitChecked(habit: Habit, position: Int, checked: Boolean) {}

        fun onHabitUpdate(habit: Habit, position: Int) {}

        fun onHabitDelete(habit: Habit, position: Int) {}

    }

    override fun onBindViewHolder(
        model: Habit,
        bundle: Bundle,
        position: Int,
        listener: BasicRecyclerViewAdapter.BaseListener?
    ) {
        b.habit = model
        b.position = position
        b.listener = listener as HabitClickListener
        b.isUpdate = bundle.getBoolean("isUpdate")

        //OnChecked Changed
        b.habitCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked)
                listener.onHabitChecked(model, position, true)
            else listener.onHabitChecked(model, position, false)
        }
    }

}