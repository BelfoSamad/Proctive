package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentTodoBinding
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.custom.TooltipWindow
import net.roeia.proctive.ui.viewmodels.todo.TodoViewModel
import net.roeia.proctive.ui.views.tabs.TodoBottomSheet
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter

@AndroidEntryPoint
class TodoFragment : Fragment() {
    companion object {
        private const val TAG = "TodoFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: TodoViewModel by viewModels()
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private val todoId: Long? = null
    private var pageType: Int? = null
    private val todoClickListener = object : ViewHolder.TodoClickListener {

        override fun onSubTodos(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.navigate_todo, bundle)
        }

        override fun onTodoClicked(todo: Todo) {
            val todoModal = TodoBottomSheet(
                TodoType.fromInt(pageType!!),
                true,
                todo,
                object : TodoBottomSheet.TodoActions {
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
                        (binding.todoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).removeItem(
                            todo
                        )
                    }

                    override fun onSubtaskChecked(todo: Todo, subtask: String, checked: Boolean) {
                        viewModel.subtaskChecked(todoId, subtask, checked)
                    }

                })
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
        //pageType = requireArguments().getInt("PAGE_TYPE")
        //todoId = requireArguments().getLong("todoId")
        pageType = 3

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
        if (todoId != null)
            viewModel.fetchTodoById(todoId)
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
        when (pageType) {
            TodoType.Goal.ordinal, TodoType.SubGoal.ordinal -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.green_300, null))
                binding.back.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
                binding.goStats.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
                binding.addTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.green_500)
                if (pageType == TodoType.Goal.ordinal)
                    binding.pageTitle.text = "My Goals"
                else binding.pageTitle.text = "SubGoals"
                binding.backWeek.visibility = GONE
                binding.startWeek.visibility = GONE
                binding.endWeek.visibility = GONE
                binding.to.visibility = GONE
                binding.forwardWeek.visibility = GONE
                binding.pomodoroWidget.visibility = GONE
            }
            TodoType.WeeklyGoal.ordinal -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.yellow_300, null))
                binding.back.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
                binding.goStats.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
                binding.addTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.yellow_500)
                binding.pageTitle.text = "Weekly Goals"
                binding.backWeek.visibility = VISIBLE
                binding.startWeek.visibility = VISIBLE
                binding.endWeek.visibility = VISIBLE
                binding.to.visibility = VISIBLE
                binding.forwardWeek.visibility = VISIBLE
                binding.pomodoroWidget.visibility = GONE
                initWeekPicker(viewModel.getCurrentWeek())
            }
            TodoType.Task.ordinal -> {
                binding.root.setBackgroundColor(resources.getColor(R.color.blue_300, null))
                binding.back.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
                binding.goStats.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
                binding.addTodo.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
                binding.pomodoroWidget.backgroundTintList =
                    AppCompatResources.getColorStateList(requireContext(), R.color.blue_500)
                binding.pageTitle.text = "My Tasks"
                binding.backWeek.visibility = GONE
                binding.startWeek.visibility = GONE
                binding.endWeek.visibility = GONE
                binding.to.visibility = GONE
                binding.forwardWeek.visibility = GONE
                binding.pomodoroWidget.visibility = VISIBLE
            }
        }

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
        val adapter = BasicRecyclerViewAdapter.Builder(
            itemList = todoList.toMutableList(),
            layoutId = R.layout.recyclerview_todo_item,
            vhClass = ViewHolder::class.java,
            bundle = bundle,
            listener = todoClickListener
        ).build()
        binding.todoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.todoList.adapter = adapter
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
            val tooltip = TooltipWindow(requireContext())
            tooltip.showTooltip(binding.sort)
        }

        //Filter
        binding.filter.setOnClickListener {
            //TODO: Open Filter Dialog
        }
    }

    /***********************************************************************************************
     * ************************* RecyclerView Adapter
     */
    class ViewHolder constructor(private val b: RecyclerviewTodoItemBinding) :
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
            listener: BasicRecyclerViewAdapter.BaseListener
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
}