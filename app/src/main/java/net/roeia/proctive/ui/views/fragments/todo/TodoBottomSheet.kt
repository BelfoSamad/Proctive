package net.roeia.proctive.ui.views.fragments.todo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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

class TodoBottomSheet(private val pageType: TodoType, private val todo: Todo) :
    BottomSheetDialogFragment() {
    companion object {
        private const val TAG = "TodoBottomSheet"
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
                binding.root.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_300)
                binding.editTask.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
                binding.deleteTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
            }
            TodoType.WeeklyGoal -> {
                binding.root.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_300)
                binding.editTask.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
                binding.deleteTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
            }
            TodoType.Task -> {
                binding.root.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_300)
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
            if (todo.goalRef == null)
                binding.todoDescription.text = todo.description
            else {
                var description = todo.description
                if (todo.description?.last().toString() != ".") description = "$description."
                val spannable = SpannableStringBuilder(description + " " + todo.goalRef.toString())
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        //TODO: Go to Goal Reference
                    }
                }
                spannable.setSpan(
                    clickableSpan,
                    description!!.length + 1, // start
                    spannable.length, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                binding.todoDescription.text = spannable
            }
        }

        //Init ListView
        if (pageType == TodoType.Task) {
            if (todo.subTasks != null)
                (binding.subTasks as ListView).adapter =
                    SubTasksArrayAdapter(requireContext(), todo.subTasks!!)
            else binding.subTasks.visibility = GONE
        }
    }
}