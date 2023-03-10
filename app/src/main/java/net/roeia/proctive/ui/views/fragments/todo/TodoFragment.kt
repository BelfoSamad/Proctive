package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
import net.roeia.proctive.base.ui.BaseBottomSheet
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentTodoBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.viewmodels.todo.TodoViewModel
import net.roeia.proctive.ui.views.viewholders.recyclerviews.TodoViewHolder
import net.roeia.proctive.base.ui.BaseRecyclerViewAdapter
import net.roeia.proctive.ui.views.viewholders.bottomsheets.BottomTodoViewHolder

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
    private var todoAdapter: BaseRecyclerViewAdapter<Todo, TodoViewHolder>? = null
    private var todoModal: BaseBottomSheet<Todo, BottomTodoViewHolder>? = null
    private var todoId: Long? = null
    private var pageType: Int? = null

    //Listeners
    private val todoDetailsListener = object : BottomTodoViewHolder.TodoActions {
        override fun onReferenceClicked(todo: Todo) {
            val bundle = Bundle()
            bundle.putBoolean("isEditable", false)
            bundle.putInt("PAGE_TYPE", pageType!!)
            todoModal = BaseBottomSheet.Builder(
                layoutId = R.layout.bottom_sheet_task,
                listener = null,
                vhClass = BottomTodoViewHolder::class.java,
                item = todo,
                bundle = bundle
            ).build()
            todoModal?.show(childFragmentManager, "TodoBottomSheetRef")
        }

        override fun onEditTodo(todo: Todo) {
            todoModal?.dismiss()
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.manage_todo, bundle)
        }

        override fun onDeleteTodo(todo: Todo) {
            todoModal?.dismiss()
            viewModel.deleteTodo(todo)
            todoAdapter?.removeItem(todo)
        }

        override fun onSubtaskChecked(todo: Todo, subtask: String, checked: Boolean) {
            viewModel.subtaskChecked(todo, subtask, checked)
        }

    }
    private val todoClickListener = object : TodoViewHolder.TodoClickListener {
        override fun onSubTodos(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", TodoType.SubGoal.ordinal)
            findNavController().navigate(R.id.navigate_todo, bundle)
        }

        override fun onTodoClicked(todo: Todo) {
            val bundle = Bundle()
            bundle.putBoolean("isEditable", true)
            bundle.putInt("PAGE_TYPE", pageType!!)
            todoModal = BaseBottomSheet.Builder(
                layoutId = R.layout.bottom_sheet_task,
                listener = todoDetailsListener,
                vhClass = BottomTodoViewHolder::class.java,
                item = todo,
                bundle = bundle
            ).build()
            todoModal?.show(childFragmentManager, "TodoBottomSheet")
        }

        override fun setPomodoroTodo(todo: Todo) {
            //TODO: Handle Pomodoro Widget
        }

        override fun onTodoChecked(todo: Todo, position: Int) {
            viewModel.setChecked(todo, true)
        }

        override fun onTodoUnChecked(todo: Todo, position: Int) {
            viewModel.setChecked(todo, false)
        }
    }

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Arguments
        pageType = requireArguments().getInt("PAGE_TYPE")
        todoId = requireArguments().getLong("todoId", -1)

        //Get TodoList
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateTodos.collect {
                    if (it.status == Status.SUCCESS) {
                        if (it.todo != null) {
                            val title = "SubGoal\nof ${it.todo.name}"
                            val span = SpannableString(title)
                            span.setSpan(
                                RelativeSizeSpan(0.5f),
                                7,
                                title.length,
                                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                            )
                            binding.pageTitle.text = span
                        }
                        initTodosRecyclerView(it.todoList!!)
                        binding.errorText.visibility = GONE
                    }
                }
            }
        }
        if (todoId != (-1).toLong()) viewModel.fetchTodoById(todoId!!)
        else if(TodoType.fromInt(pageType!!) != TodoType.WeeklyGoal) viewModel.fetchByType(TodoType.fromInt(pageType!!))
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
        todoAdapter = BaseRecyclerViewAdapter.Builder(
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

        //Add Task
        binding.addTodo.setOnClickListener {
            val bundle = Bundle()
            if (todoId!! != (-1).toLong()) {
                bundle.putInt("PAGE_TYPE", TodoType.SubGoal.ordinal)
                bundle.putLong("goalId", todoId!!)
            } else bundle.putInt("PAGE_TYPE", pageType!!)
            if (TodoType.fromInt(pageType!!) == TodoType.WeeklyGoal)
                bundle.putLong("midWeek", viewModel.getMidWeek()!!)
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