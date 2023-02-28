package net.roeia.proctive.ui.views.fragments.todo

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.annotation.Keep
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.OnCheckedStateChangeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentTodoBinding
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.viewmodels.todo.TodoViewModel
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter
import net.roeia.proctive.utils.adapters.SubTasksArrayAdapter

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

        override fun onReferenceClicked(todo: Todo) {
            //TODO: Show it as Dialog instead
        }

        override fun onTodoClicked(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.navigate_todo, bundle)
        }

        override fun onEditTodo(todo: Todo) {
            val bundle = Bundle()
            bundle.putLong("todoId", todo.todoId!!)
            bundle.putInt("PAGE_TYPE", pageType!!)
            findNavController().navigate(R.id.manage_todo, bundle)
        }

        override fun setPomodoroTodo(todo: Todo) {
            //TODO: Handle Pomodoro Widget
        }

        override fun onTodoChecked(todo: Todo) {
            (binding.todoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).removeItem(todo)
            viewModel.setChecked(todo.todoId!!, true)
            (binding.checkedTodoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).addItem(
                todo
            )
            binding.checkedError.visibility = VISIBLE
            binding.checkedTodoList.visibility = VISIBLE
            binding.clearChecked.visibility = VISIBLE
            binding.checkedToggle.visibility = VISIBLE
            binding.checkedToggle.tag = 1
            binding.checkedToggle.setImageResource(R.drawable.collapse)
        }

        override fun onTodoUnChecked(todo: Todo) {
            (binding.todoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).addItem(todo)
            viewModel.setChecked(todo.todoId!!, false)
            (binding.checkedTodoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).removeItem(
                todo
            )
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
                        initCheckedTodosRecyclerView(it.checkedTodoList!!)
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
                binding.root.setBackgroundColor(resources.getColor(R.color.successColor, null))
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
                binding.root.setBackgroundColor(resources.getColor(R.color.primaryColor, null))
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
                binding.root.setBackgroundColor(resources.getColor(R.color.white, null))
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

    private fun initCheckedTodosRecyclerView(todoList: List<Todo>?) {
        val bundle = Bundle()
        bundle.putInt("PageType", pageType!!)
        val adapter = BasicRecyclerViewAdapter.Builder(
            itemList = todoList!!.toMutableList(),
            layoutId = R.layout.recyclerview_todo_item,
            vhClass = ViewHolder::class.java,
            bundle = bundle,
            listener = todoClickListener
        ).build()
        binding.checkedTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.checkedTodoList.adapter = adapter
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

        //Clear Checked List
        binding.clearChecked.setOnClickListener {
            //TODO: Use Dialog for Confirmation
            viewModel.clearUnCheckedTodos(
                (binding.checkedTodoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>)
                    .getItems().toList()
            )
            (binding.checkedTodoList.adapter as BasicRecyclerViewAdapter<Todo, ViewHolder>).clearItems()
            binding.checkedTasksText.visibility = VISIBLE
            binding.checkedError.visibility = VISIBLE
            binding.checkedTodoList.visibility = GONE
            binding.checkedToggle.visibility = GONE
            binding.clearChecked.visibility = GONE
        }

        //Toggle Checked List
        binding.checkedToggle.setOnClickListener {
            if (it.tag == 0) {
                binding.checkedTodoList.visibility = VISIBLE
                binding.checkedError.visibility = GONE
                it.tag = 1
                binding.checkedToggle.setImageResource(R.drawable.collapse)
                binding.todoPageContainer.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
            } else {
                binding.checkedTodoList.visibility = GONE
                binding.checkedError.visibility = VISIBLE
                it.tag = 0
                binding.checkedToggle.setImageResource(R.drawable.expand)
                binding.todoPageContainer.layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
            }
        }
    }

    /***********************************************************************************************
     * ************************* RecyclerView Adapter
     */
    class ViewHolder @Keep constructor(private val b: RecyclerviewTodoItemBinding) :
        BasicRecyclerViewAdapter.BaseViewHolder<Todo>(b) {

        interface TodoClickListener : BasicRecyclerViewAdapter.BaseListener {

            fun onReferenceClicked(todo: Todo)

            fun onTodoClicked(todo: Todo)

            fun onEditTodo(todo: Todo)

            fun setPomodoroTodo(todo: Todo)

            fun onTodoChecked(todo: Todo)

            fun onTodoUnChecked(todo: Todo)

        }

        override fun onBindViewHolder(
            model: Todo,
            bundle: Bundle,
            listener: BasicRecyclerViewAdapter.BaseListener
        ) {
            b.type = TodoType.fromInt(bundle.getInt("PageType"))
            b.todo = model
            b.listener = listener as TodoClickListener

            //Init Labels List
            for (label in model.labels!!) {
                val chip = HorizontalScrollView.inflate(
                    b.root.context,
                    R.layout.single_chip_option,
                    null
                ) as Chip
                chip.text = label
                b.labels.addView(chip)
            }

            //Set Description and Goal Reference
            if (model.description != null) {
                if (model.goalRef == null)
                    b.todoDescription.text = model.description
                else {
                    var description = model.description
                    if (model.description?.last().toString() != ".") description = "$description."
                    val spannable = SpannableStringBuilder(description + " " + model.goalRef.toString())
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            listener.onReferenceClicked(model)
                        }
                    }
                    spannable.setSpan(
                        clickableSpan,
                        description!!.length + 1, // start
                        spannable.length, // end
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                    b.todoDescription.text = spannable
                }
            }

            //OnChecked Changed
            b.todoCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
                if (checkBox.isChecked)
                    listener.onTodoChecked(model)
                else listener.onTodoUnChecked(model)
            }

            //Collapse
            b.labels.visibility = GONE
            b.editTask.visibility = GONE
            b.bottomBackground.visibility = GONE
            b.goTodo.visibility = GONE
            b.divider.visibility = GONE
            b.collapseTodo.setOnClickListener {
                if (it.tag == 0) {
                    b.labels.visibility = VISIBLE
                    b.editTask.visibility = VISIBLE
                    b.bottomBackground.visibility = VISIBLE
                    b.goTodo.visibility = VISIBLE
                    b.divider.visibility = VISIBLE
                    it.tag = 1
                    b.collapseTodo.setImageResource(R.drawable.collapse)
                    b.todoContainer.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
                } else {
                    b.labels.visibility = GONE
                    b.editTask.visibility = GONE
                    b.bottomBackground.visibility = GONE
                    b.goTodo.visibility = GONE
                    b.divider.visibility = GONE
                    it.tag = 0
                    b.collapseTodo.setImageResource(R.drawable.expand)
                    b.todoContainer.layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
                }
            }

            //Init ListView
            if (TodoType.fromInt(bundle.getInt("PageType")) == TodoType.Task) {
                (b.subTasks as ListView).adapter =
                    SubTasksArrayAdapter(b.root.context, model.subTasks!!)
            }
        }
    }
}