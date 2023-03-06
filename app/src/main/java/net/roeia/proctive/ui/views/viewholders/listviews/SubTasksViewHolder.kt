package net.roeia.proctive.ui.views.viewholders.listviews

import android.os.Bundle
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.ListTaskItemBinding
import net.roeia.proctive.models.pojo.TodoChecked

class SubTasksViewHolder constructor(val binding: ListTaskItemBinding) :
    BaseViewHolder<TodoChecked>(binding) {

    interface SubTaskActions : BaseListener {

        fun onDeleteSubTask(subtask: TodoChecked) {
            //Nothing
        }

        fun onCheckSubTask(subtask: String, checked: Boolean) {
            //Nothing
        }

    }

    override fun onBindViewHolder(
        model: TodoChecked?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.todo = model
        binding.isChecked = bundle.getBoolean("isChecked")
        binding.listener = listener as SubTaskActions
    }

}