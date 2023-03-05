package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentTodoBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.viewmodels.todo.TodoViewModel
import net.roeia.proctive.ui.views.viewholders.TodoBottomSheet
import net.roeia.proctive.ui.views.viewholders.recyclerviews.TodoViewHolder
import net.roeia.proctive.utils.BasicRecyclerViewAdapter

@AndroidEntryPoint
class TodoFragment : Fragment() {
    companion object {
        private const val TAG = "TodoFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    //Init
    private val viewModel: TodoViewModel by viewModels()
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    //Data
    private var todoAdapter: BasicRecyclerViewAdapter<Todo, TodoViewHolder>? = null
    private val todoId: Long? = null
    private var pageType: Int? = null

    //Listeners
    private val todoDetailsListener = object : TodoBottomSheet.TodoActions {
        override fun onReferenceClicked(todo: Todo) {
            TodoBottomSheet(TodoType.fromInt(pageType!!), false, todo, null).show(
                childFragmentManager,
                "TodoBottomSheetRef"
            )
        }

        override fun onEditTodo(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.manage_todo, bundle)
        }

        override fun onDeleteTodo(todo: Todo) {
            viewModel.deleteTodo(todo)
            todoAdapter?.removeItem(todo)
        }

        override fun onSubtaskChecked(todo: Todo, subtask: String, checked: Boolean) {
            viewModel.subtaskChecked(todoId, subtask, checked)
        }

    }
    private val todoClickListener = object : TodoViewHolder.TodoClickListener {
        override fun onSubTodos(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.navigate_todo, bundle)
        }

        override fun onTodoClicked(todo: Todo) {
            val todoModal =
                TodoBottomSheet(TodoType.fromInt(pageType!!), true, todo, todoDetailsListener)
            todoModal.show(childFragmentManager, "TodoBottomSheet")
        }

        override fun setPomodoroTodo(todo: Todo) {
            //TODO: Handle Pomodoro Widget
        }

        override fun onTodoChecked(todo: Todo, position: Int) {
            viewModel.setChecked(todo.todoId!!, true)
        }

        override fun onTodoUnChecked(todo: Todo, position: Int) {
            viewModel.setChecked(todo.todoId!!, false)
        }
    }

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Page Type, TODO: use argument
        pageType = requireArguments().getInt("PAGE_TYPE")
        //todoId = requireArguments().getLong("todoId")
        //pageType = 3

        //Get TodoList
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateTodos.collect {
                    if (it.status == Status.SUCCESS) {
                        initTodosRecyclerView(it.todoList!!)
                    }
                }
            }
        }
        if (todoId != null) viewModel.fetchTodoById(todoId)
        else viewModel.fetchByType(TodoType.fromInt(pageType!!))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Reform Page
        binding.type = TodoType.fromInt(pageType!!)
        binding.pageTitle.text = resources.getStringArray(R.array.todo_title)[pageType!!]
        if (TodoType.fromInt(pageType!!) == TodoType.WeeklyGoal)
            initWeekPicker(viewModel.getCurrentWeek())

        //Init ClickListeners
        initClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initWeekPicker(currentWeek: String) {
        //Set Current Week
        binding.startWeek.text = currentWeek.split(",")[0]
        binding.endWeek.text = currentWeek.split(",")[1]

        //Back
        binding.backWeek.setOnClickListener {
            val week = viewModel.getLastWeek()
            binding.startWeek.text = week.split(",")[0]
            binding.endWeek.text = week.split(",")[1]
        }

        //Forward
        binding.forwardWeek.setOnClickListener {
            val week = viewModel.getNextWeek()
            binding.startWeek.text = week.split(",")[0]
            binding.endWeek.text = week.split(",")[1]
        }
    }

    private fun initTodosRecyclerView(todoList: List<Todo>) {
        val bundle = Bundle()
        bundle.putInt("PageType", pageType!!)
        todoAdapter = BasicRecyclerViewAdapter.Builder(
            itemList = todoList.toMutableList(),
            layoutId = R.layout.recyclerview_todo_item,
            vhClass = TodoViewHolder::class.java,
            bundle = bundle,
            listener = todoClickListener
        ).build()
        binding.todoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.todoList.adapter = todoAdapter
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Stats
        binding.goStats.setOnClickListener {
            findNavController().navigate(R.id.todo_tracking)
        }

        //Add Task
        binding.addTodo.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.manage_todo, bundle)
        }

        //Sort
        binding.sort.setOnClickListener {
            //TODO: Open Sort Dialog
        }

        //Filter
        binding.filter.setOnClickListener {
            //TODO: Open Filter Dialog
        }
    }
}