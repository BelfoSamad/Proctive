package net.roeia.proctive.ui.views.viewholders.bottomsheets

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import com.google.android.material.chip.Chip
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseArrayAdapter
import net.roeia.proctive.base.ui.BaseViewHolder
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.models.pojo.TodoChecked
import net.roeia.proctive.ui.views.viewholders.listviews.SubTasksViewHolder

class TodoViewHolder constructor(private val binding: BottomSheetTaskBinding) :
    BaseViewHolder<Todo>(binding) {

    interface TodoActions : BaseListener {

        fun onReferenceClicked(todo: Todo)

        fun onEditTodo(todo: Todo)

        fun onDeleteTodo(todo: Todo)

        fun onSubtaskChecked(todo: Todo, subtask: String, checked: Boolean)

    }

    override fun onBindViewHolder(
        model: Todo?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        val pageType = TodoType.fromInt(bundle.getInt("PAGE_TYPE"))
        binding.type = pageType
        binding.todo = model
        binding.isEditable = bundle.getBoolean("isEditable")
        binding.listener = listener as TodoActions

        //Init Labels List
        for (label in model?.labels!!) {
            val chip = HorizontalScrollView.inflate(
                binding.root.context,
                R.layout.single_chip_option,
                null
            ) as Chip
            chip.text = label
            when (pageType) {
                TodoType.Goal, TodoType.SubGoal -> {
                    chip.setTextColor(binding.root.resources.getColor(R.color.green_300, null))
                    chip.setChipBackgroundColorResource(R.color.green_700)
                }
                TodoType.WeeklyGoal -> {
                    chip.setTextColor(binding.root.resources.getColor(R.color.yellow_300, null))
                    chip.setChipBackgroundColorResource(R.color.yellow_700)
                }
                TodoType.Task -> {
                    chip.setTextColor(binding.root.resources.getColor(R.color.blue_300, null))
                    chip.setChipBackgroundColorResource(R.color.blue_700)
                }
            }
            binding.labels.addView(chip)
        }

        //Set Description and Goal Reference
        if (model.description != null) {
            if (model.goalRef == null || pageType != TodoType.WeeklyGoal)
                binding.todoDescription.text = model.description
            else {
                var description = model.description
                if (model.description?.last().toString() != ".") description = "$description."
                val spannable = SpannableStringBuilder(description + " " + model.goalRef.toString())
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        listener.onReferenceClicked(model.refTodo!!)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        when (pageType) {
                            TodoType.Goal, TodoType.SubGoal -> {
                                ds.color = binding.root.resources.getColor(R.color.green_700, null)
                            }
                            TodoType.WeeklyGoal -> {
                                ds.color = binding.root.resources.getColor(R.color.yellow_700, null)
                            }
                            TodoType.Task -> {
                                ds.color = binding.root.resources.getColor(R.color.blue_700, null)
                            }
                        }
                    }
                }
                spannable.setSpan(
                    clickableSpan,
                    description!!.length + 1, // start
                    spannable.length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.todoDescription.movementMethod = LinkMovementMethod.getInstance()
                binding.todoDescription.text = spannable
            }
        }

        //Init ListView
        if (pageType == TodoType.Task) {
            if (model.subTasks != null) {
                //Update ListView
                val b = Bundle()
                bundle.putBoolean("isChecked", true)
                val adapter = BaseArrayAdapter.Builder(
                    itemsList = model.subTasks!!.map { TodoChecked(it.key, it.value) }
                        .toMutableList(),
                    layoutId = R.layout.list_task_item,
                    vhClass = SubTasksViewHolder::class.java,
                    bundle = bundle,
                    listener = object : SubTasksViewHolder.SubTaskActions {
                        override fun onCheckSubTask(subtask: String, checked: Boolean) {
                            listener.onSubtaskChecked(model, subtask, checked)
                        }
                    }
                ).build()
                (binding.subTasks as ListView).adapter = adapter
            } else binding.subTasks.visibility = View.GONE
        }
    }

}