package net.roeia.proctive.ui.views.viewholders

import android.os.Bundle
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter

class TodoViewHolder constructor(private val b: RecyclerviewTodoItemBinding) :
    BasicRecyclerViewAdapter.BaseViewHolder<Todo>(b) {

    interface TodoClickListener : BasicRecyclerViewAdapter.BaseListener {

        fun onSubTodos(todo: Todo)

        fun onTodoClicked(todo: Todo)

        fun onTodoChecked(todo: Todo, position: Int)

        fun onTodoUnChecked(todo: Todo, position: Int)

        fun setPomodoroTodo(todo: Todo)

    }

    override fun onBindViewHolder(
        model: Todo,
        bundle: Bundle,
        position: Int,
        listener: BasicRecyclerViewAdapter.BaseListener?
    ) {
        b.type = TodoType.fromInt(bundle.getInt("PageType"))
        b.todo = model
        b.listener = listener as TodoClickListener

        //OnChecked Changed
        b.todoCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked)
                listener.onTodoChecked(model, position)
            else listener.onTodoUnChecked(model, position)
        }
    }
}