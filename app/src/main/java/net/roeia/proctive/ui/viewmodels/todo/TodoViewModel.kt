package net.roeia.proctive.ui.viewmodels.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.roeia.proctive.data.Status
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "TodoViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val dateFormat = SimpleDateFormat("dd-MM", Locale.ROOT)
    private val cal: Calendar = Calendar.getInstance()

    //UI States
    private val _uiStateTodos = MutableStateFlow(TodosUIState())
    val uiStateTodos: StateFlow<TodosUIState> = _uiStateTodos.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchByType(pageType: TodoType) {
        viewModelScope.launch {
            //TODO (Backend): Get List of Todo based of type -> Split it based on checked
            _uiStateTodos.update {
                it.copy(
                    status = Status.SUCCESS,
                    todoList = listOf(
                        Todo(
                            todoId = 1L,
                            name = "Publish Proctive",
                            labels = listOf("Android", "Application"),
                            description = "This is the description of the goal with the title Publish Proctive.",
                            type = TodoType.Goal,
                            isChecked = false,
                            due = Date(),
                            pomodoroAverage = 5,
                            subTasks = mapOf("Finish UI" to true, "Finish Backend" to false, "Publishing Preparations" to true),
                            goalRef = 112133443L
                        ),
                        Todo(
                            todoId = 1L,
                            name = "Publish Rehlla",
                            labels = listOf("Android", "Application"),
                            description = "This is the description of the goal with the title Publish Rehlla.",
                            type = TodoType.Goal,
                            isChecked = false,
                            due = Date(),
                            pomodoroAverage = 5,
                            subTasks = mapOf("Finish UI" to true, "Finish Backend" to false, "Publishing Preparations" to false),
                            goalRef = 112133443L
                        )
                    ),
                    checkedTodoList = listOf(
                        Todo(
                            todoId = 1L,
                            name = "Publish Proctive",
                            labels = listOf("Android", "Application"),
                            description = "This is the description of the goal with the title Publish Proctive.",
                            type = TodoType.Goal,
                            isChecked = false,
                            due = Date(),
                            pomodoroAverage = 5,
                            subTasks = mapOf("Finish UI" to true, "Finish Backend" to false, "Publishing Preparations" to false),
                            goalRef = 112133443L
                        ),
                        Todo(
                            todoId = 1L,
                            name = "Publish Rehlla",
                            labels = listOf("Android", "Application"),
                            description = "This is the description of the goal with the title Publish Rehlla.",
                            type = TodoType.Goal,
                            isChecked = false,
                            due = Date(),
                            pomodoroAverage = 5,
                            subTasks = mapOf("Finish UI" to true, "Finish Backend" to false, "Publishing Preparations" to false),
                            goalRef = 112133443L
                        )
                    )
                )
            }
        }
    }

    fun fetchTodoById(todoId: Long) {
        fetchByType(TodoType.Goal)
        //TODO (Backend): Fetch Todo by Id and return subtasks
    }

    fun getCurrentWeek(): String {
        //TODO (Backend): Set first day based on user's preference (Default: Sun)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        return "$startWeek,$endWeek"
    }

    fun getLastWeek(): String {
        cal.time = dateFormat.parse(state["START_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, -7)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.time = dateFormat.parse(state["END_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, -7)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        return "$startWeek,$endWeek"
    }

    fun getNextWeek(): String {
        cal.time = dateFormat.parse(state["START_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.time = dateFormat.parse(state["END_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        return "$startWeek,$endWeek"
    }

    fun setChecked(todoId: Long, checked: Boolean) {
        //TODO: Set Todo as Checked/UnChecked
    }

    fun deleteTodo(todo: Todo) {
        //TODO: Delete Todo
    }

    fun subtaskChecked(todoId: Long?, subtask: String, checked: Boolean) {
        //TODO: Set SubTask Checked on todoId
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class TodosUIState(
        val status: Status = Status.LOADING,
        val todoList: List<Todo>? = null,
        val checkedTodoList: List<Todo>? = null,
    )
}