package net.roeia.proctive.ui.views.viewholders

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import net.roeia.proctive.R
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.utils.adapters.SubTasksArrayAdapter

class TodoBottomSheet(
    private val pageType: TodoType,
    private val editable: Boolean,
    private val todo: Todo,
    private val listener: TodoActions?
) : BottomSheetDialogFragment() {
    companion object {
        private const val TAG = "TodoBottomSheet"
    }

    interface TodoActions {

        fun onReferenceClicked(todo: Todo)

        fun onEditTodo(todo: Todo)

        fun onDeleteTodo(todo: Todo)

        fun onSubtaskChecked(todo: Todo, subtask: String, checked: Boolean)

    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var _binding: BottomSheetTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Background Color
        when (pageType) {
            TodoType.Goal, TodoType.SubGoal -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.green_300, null))
                binding.editTask.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
                binding.deleteTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
            }
            TodoType.WeeklyGoal -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.yellow_300, null))
                binding.editTask.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
                binding.deleteTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
            }
            TodoType.Task -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.blue_300, null))
                binding.editTask.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
                binding.deleteTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
            }
        }

        //Init Labels List
        for (label in todo.labels!!) {
            val chip = HorizontalScrollView.inflate(
                requireContext(),
                R.layout.single_chip_option,
                null
            ) as Chip
            chip.text = label
            when (pageType) {
                TodoType.Goal, TodoType.SubGoal -> {
                    chip.setTextColor(resources.getColor(R.color.green_300, null))
                    chip.setChipBackgroundColorResource(R.color.green_700)
                }
                TodoType.WeeklyGoal -> {
                    chip.setTextColor(resources.getColor(R.color.yellow_300, null))
                    chip.setChipBackgroundColorResource(R.color.yellow_700)
                }
                TodoType.Task -> {
                    chip.setTextColor(resources.getColor(R.color.blue_300, null))
                    chip.setChipBackgroundColorResource(R.color.blue_700)
                }
            }
            binding.labels.addView(chip)
        }

        //Set Description and Goal Reference
        if (todo.description != null) {
            if (todo.goalRef == null || pageType != TodoType.WeeklyGoal)
                binding.todoDescription.text = todo.description
            else {
                var description = todo.description
                if (todo.description?.last().toString() != ".") description = "$description."
                val spannable = SpannableStringBuilder(description + " " + todo.goalRef.toString())
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        dismiss()
                        listener?.onReferenceClicked(todo.refTodo!!)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        when (pageType) {
                            TodoType.Goal, TodoType.SubGoal -> {
                                ds.color = resources.getColor(R.color.green_700, null)
                            }
                            TodoType.WeeklyGoal -> {
                                ds.color = resources.getColor(R.color.yellow_700, null)
                            }
                            TodoType.Task -> {
                                ds.color = resources.getColor(R.color.blue_700, null)
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

        binding.deleteTodo.visibility = if (editable) VISIBLE else GONE
        binding.editTask.visibility = if (editable) VISIBLE else GONE

        binding.deleteTodo.setOnClickListener {
            listener?.onDeleteTodo(todo)
            dismiss()
        }

        binding.editTask.setOnClickListener {
            listener?.onEditTodo(todo)
            dismiss()
        }

        //Init ListView
        if (pageType == TodoType.Task) {
            if (todo.subTasks != null)
                (binding.subTasks as ListView).adapter =
                    SubTasksArrayAdapter(
                        requireContext(),
                        todo.subTasks!!.toMutableMap(),
                        true,
                        object: SubTasksArrayAdapter.SubTaskActions {
                            override fun onCheckSubTask(subtask: String, checked: Boolean) {
                                listener?.onSubtaskChecked(todo, subtask, checked)
                            }
                        }
                    )
            else binding.subTasks.visibility = GONE
        }
    }
}