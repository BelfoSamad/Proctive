package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseArrayAdapter
import net.roeia.proctive.base.ui.BaseDialog
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentManageTodoBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.models.pojo.TodoChecked
import net.roeia.proctive.ui.custom.LabeledInputView
import net.roeia.proctive.ui.viewmodels.todo.ManageTodoViewModel
import net.roeia.proctive.ui.views.viewholders.dialogs.DateTimePickerViewHolder
import net.roeia.proctive.ui.views.viewholders.listviews.SubTasksViewHolder
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ManageTodoFragment : Fragment() {
    companion object {
        private const val TAG = "ManageTodoFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: ManageTodoViewModel by viewModels()
    private var _binding: FragmentManageTodoBinding? = null
    private val binding get() = _binding!!

    //Data
    private var adapter: BaseArrayAdapter<TodoChecked, SubTasksViewHolder>? = null
    private var dialog: BaseDialog<Date, DateTimePickerViewHolder>? = null
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ROOT)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    private var todoId: Long? = null
    private var goalId: Long? = null
    private var pageType: Int? = null
    private var midWeek: Long? = null

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Arguments
        pageType = requireArguments().getInt("PAGE_TYPE")
        goalId = requireArguments().getLong("goalId", -1)
        todoId = requireArguments().getLong("todoId", -1)
        midWeek = requireArguments().getLong("midWeek", -1)

        //Get TodoObject
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateTodo.collect {
                    if (it.status == Status.SUCCESS)
                        initTodo(it.todo!!)
                }
            }
        }
        if (todoId != (-1).toLong())
            viewModel.fetchTodoById(todoId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Reform Page
        binding.type = TodoType.fromInt(pageType!!)
        if (todoId == null) {
            if (pageType == TodoType.WeeklyGoal.ordinal)
                binding.todoDueDate.text = viewModel.getCurrentWeek(null)
            binding.pageTitle.text =
                "Edit ${resources.getStringArray(R.array.todo_title)[pageType!!]}"
        } else {
            binding.pageTitle.text =
                "Add ${resources.getStringArray(R.array.todo_title)[pageType!!]}"
        }

        //Handle Status Changes
        handleChipChanges()

        //Init Addable Inputs
        initAddableInputs()

        //Init ClickListeners
        initClickListeners()

        //Observe LiveData
        viewModel.todoUpdatedLiveData.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initTodo(todo: Todo) {
        binding.todoName.setValue(todo.name!!)
        binding.todoDescription.setValue(todo.description!!)
        if (TodoType.fromInt(pageType!!) == TodoType.WeeklyGoal)
            midWeek = todo.due?.time

        //set Labels
        for (label in todo.labels!!) {
            val chip = Chip(requireContext())
            chip.text = label
            binding.labels.addView(chip)
        }

        //Set Due Date
        if (todo.due != null) {
            if (pageType == TodoType.WeeklyGoal.ordinal)
                binding.todoDueDate.text = viewModel.getCurrentWeek(todo.due)
            else binding.todoDueDate.text = dateTimeFormat.format(todo.due!!)
            binding.todoDueDate.tag = 1
        }

        //Set Subtasks
        /* TODO: Fix Subtasks List
        (binding.todoSubtasks as ListView).adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_simple_item,
            R.id.item_text,
            tod.subTasks?.keys?.toList()!!
        )*/
    }

    private fun handleChipChanges() {
        if (pageType == TodoType.WeeklyGoal.ordinal) {
            binding.todoDueDate.visibility = GONE
            binding.dueDateLabel.visibility = GONE
        } else {
            //Handle Due Date
            binding.todoDueDate.setOnClickListener {
                val bundle = Bundle()
                bundle.putInt("PAGE_TYPE", pageType!!)
                bundle.putBoolean("justDate", TodoType.fromInt(pageType!!) != TodoType.Task)
                dialog = BaseDialog.Builder(
                    item = if (it.tag == 1) dateTimeFormat.parse((it as Chip).text.toString()) else null,
                    layoutId = R.layout.dialog_date_time_picker,
                    vhClass = DateTimePickerViewHolder::class.java,
                    bundle = bundle,
                    listener = object : DateTimePickerViewHolder.DateTimePickerListener {
                        override fun onDateTimePicked(due: String) {
                            binding.todoDueDate.text = due
                            binding.todoDueDate.tag = 1
                            dialog?.dismiss()
                        }
                    }
                ).build()
                dialog?.show(childFragmentManager, "DateTimePickerDialog")
            }
        }
    }

    private fun initAddableInputs() {
        //Set Labels
        binding.todoLabel.setOnAddListener(object : LabeledInputView.OnAddListener {
            override fun onValueAdded(value: String) {
                //Add Label in ViewModel
                val labels = viewModel.getLabelsList()
                if (labels == null)
                    viewModel.setLabelsList(listOf(value))
                else {
                    val mutableList = labels.toMutableList()
                    mutableList.add(value)
                    viewModel.setLabelsList(mutableList)
                }

                val chip = HorizontalScrollView.inflate(
                    requireContext(),
                    R.layout.single_chip_option,
                    null
                ) as Chip
                when (TodoType.fromInt(pageType!!)) {
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
                chip.text = value
                binding.labels.addView(chip)
            }
        })

        //Set Subtasks
        binding.subtasksLabel.setOnAddListener(object : LabeledInputView.OnAddListener {
            override fun onValueAdded(value: String) {
                //Add Subtask in ViewModel
                val subtasks = viewModel.getSubtasksList()
                if (subtasks == null)
                    viewModel.setSubtasksList(listOf(value))
                else {
                    val mutableList = subtasks.toMutableList()
                    mutableList.add(value)
                    viewModel.setSubtasksList(mutableList)
                }

                //Update ListView
                val bundle = Bundle()
                bundle.putBoolean("isChecked", false)
                adapter = BaseArrayAdapter.Builder(
                    itemsList = viewModel.getSubtasksList()!!.map { TodoChecked(it, false) }
                        .toMutableList(),
                    layoutId = R.layout.list_task_item,
                    vhClass = SubTasksViewHolder::class.java,
                    bundle = bundle,
                    listener = object : SubTasksViewHolder.SubTaskActions {
                        override fun onDeleteSubTask(subtask: TodoChecked) {
                            viewModel.removeSubtask(subtask.todo)
                            adapter?.removeItem(subtask)
                        }
                    }
                ).build()
                (binding.todoSubtasks as ListView).adapter = adapter
            }
        })
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Done (Add/Edit)
        binding.doneTodo.setOnClickListener {
            if (binding.todoName.isValid()) {
                viewModel.saveTodo(
                    pageType!!,
                    if (goalId!! == (-1).toLong()) null else goalId,//TODO: Editing subgoal
                    binding.todoName.getValue(),
                    if (binding.todoDescription.getValue() == "") null else binding.todoDescription.getValue(),
                    when(TodoType.fromInt(pageType!!)) {
                        TodoType.Task -> {
                            if (binding.todoDueDate.tag == 0) null else dateTimeFormat.parse(binding.todoDueDate.text.toString())
                        }
                        TodoType.Goal, TodoType.SubGoal -> {
                            if (binding.todoDueDate.tag == 0) null else dateFormat.parse(binding.todoDueDate.text.toString())
                        }
                        TodoType.WeeklyGoal -> {
                            Date(midWeek!!)
                        }
                    }
                )
            }
        }
    }


}