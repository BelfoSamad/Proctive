package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.os.Bundle
import android.util.Log
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.base.ui.BaseViewHolder

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
        Log.d("Abdessamad", "onBindViewHolder: " + TodoType.fromInt(bundle.getInt("PageType")))
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