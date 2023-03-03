package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentManageTodoBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.custom.LabeledInputView
import net.roeia.proctive.ui.viewmodels.todo.ManageTodoViewModel
import net.roeia.proctive.ui.views.dialogs.DateTimePickerDialog
import net.roeia.proctive.utils.adapters.SubTasksArrayAdapter
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

    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.ROOT)
    private var todoId: Long? = null
    private var pageType: Int? = null

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get TodoId TODO: use argument
        //todoId = requireArguments().getLong("todoId")
        //Get Page Type,
        pageType = requireArguments().getInt("PAGE_TYPE")
        todoId = null
        //pageType = 3

        //Get TodoObject
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateTodo.collect {
                    if (it.status == Status.SUCCESS)
                        initTodo(it.todo!!)
                }
            }
        }
        if (todoId != null)
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
            binding.deleteTodo.visibility = GONE //Delete Option
            if (pageType == TodoType.WeeklyGoal.ordinal)
                binding.todoDueDate.text = viewModel.getCurrentWeek(null)
            binding.pageTitle.text = "Edit ${resources.getStringArray(R.array.todo_title)[pageType!!]}"
        } else {
            binding.pageTitle.text = "Add ${resources.getStringArray(R.array.todo_title)[pageType!!]}"
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
        (binding.todoSubtasks as ListView).adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_simple_item,
            R.id.item_text,
            todo.subTasks?.keys?.toList()!!
        )
    }

    private fun handleChipChanges() {
        if (pageType == TodoType.WeeklyGoal.ordinal) {
            binding.todoDueDate.setOnClickListener(null)
        } else {
            //Handle Due Date
            binding.todoDueDate.setOnClickListener {
                val dialog = DateTimePickerDialog(
                    TodoType.fromInt(pageType!!),
                    if (it.tag == 1) dateTimeFormat.parse((it as Chip).text.toString()) else null,
                    object : DateTimePickerDialog.DateTimePickerListener {
                        override fun onDateTimePicked(due: String) {
                            binding.todoDueDate.text = due
                            binding.todoDueDate.tag = 1
                        }
                    })
                dialog.show(childFragmentManager, "DateTimePickerDialog")
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
                (binding.todoSubtasks as ListView).adapter =
                    SubTasksArrayAdapter(
                        requireContext(),
                        viewModel.getSubtasksList()!!.associateWith { false }.toMutableMap(),
                        false,
                        object: SubTasksArrayAdapter.SubTaskActions {
                            override fun onDeleteSubTask(subtask: String) {
                                viewModel.removeSubtask(subtask)
                            }
                        }
                    )
            }
        })
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Delete
        binding.deleteTodo.setOnClickListener {
            viewModel.deleteTodo(todoId!!)
        }

        //Done (Add/Edit)
        binding.doneTodo.setOnClickListener {
            if (binding.todoName.isValid()) {
                viewModel.saveTodo(
                    pageType!!,
                    binding.todoName.getValue(),
                    if (binding.todoDescription.getValue() == "") null else binding.todoDescription.getValue(),
                    if (binding.todoDueDate.tag == 0) null else dateTimeFormat.parse(binding.todoDueDate.text.toString()),
                )
            }
        }
    }


}