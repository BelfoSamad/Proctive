package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.os.Bundle
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.utils.BaseViewHolder

class TodoViewHolder constructor(private val binding: RecyclerviewTodoItemBinding) :
    BaseViewHolder<Todo>(binding) {

    interface TodoClickListener : BaseListener {

        fun onSubTodos(todo: Todo)

        fun onTodoClicked(todo: Todo)

        fun onTodoChecked(todo: Todo, position: Int)

        fun onTodoUnChecked(todo: Todo, position: Int)

        fun setPomodoroTodo(todo: Todo)

    }

    override fun onBindViewHolder(
        model: Todo?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        binding.type = TodoType.fromInt(bundle.getInt("PageType"))
        binding.todo = model
        binding.listener = listener as TodoClickListener

        //OnChecked Changed
        binding.todoCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked)
                listener.onTodoChecked(model!!, position)
            else listener.onTodoUnChecked(model!!, position)
        }
    }
}