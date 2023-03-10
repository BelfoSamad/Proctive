package net.roeia.proctive.ui.viewmodels.todo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.roeia.proctive.data.Status
import net.roeia.proctive.data.repositories.TodoRepository
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val todoRepository: TodoRepository
) : ViewModel() {
    companion object {
        private const val TAG = "TodoViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
    private val cal: Calendar = Calendar.getInstance()

    //UI States
    private val _uiStateTodos = MutableStateFlow(TodosUIState())
    val uiStateTodos: StateFlow<TodosUIState> = _uiStateTodos.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchByType(todoType: TodoType) {
        viewModelScope.launch {
            todoRepository.fetchTodos(todoType).collect { todos ->
                _uiStateTodos.update {
                    it.copy(
                        status = Status.SUCCESS,
                        todoList = todos
                    )
                }
            }
        }
    }

    private fun fetchByWeek(startWeek: String, endWeek: String) {
        Log.d(TAG, "fetchByWeek: $startWeek")
        Log.d(TAG, "fetchByWeek: $endWeek")
        viewModelScope.launch {
            todoRepository.fetchTodosByWeek(dateFormat.parse(startWeek), dateFormat.parse(endWeek)).collect { todos ->
                Log.d(TAG, "fetchByWeek: Pushing")
                _uiStateTodos.update {
                    it.copy(
                        status = Status.SUCCESS,
                        todoList = todos
                    )
                }
            }
        }
    }

    fun fetchTodoById(todoId: Long) {
        viewModelScope.launch {
            todoRepository.fetchTodoById(todoId).collect { todo ->
                _uiStateTodos.update {
                    it.copy(
                        status = Status.SUCCESS,
                        todo = todo,
                        todoList = todo.subGoals
                    )
                }
            }
        }
    }

    fun getCurrentWeek(): String {
        //TODO (Backend): Set first day based on user's preference (Default: Sun)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.add(Calendar.DAY_OF_WEEK, 3)
        state["MID_WEEK"] = cal.time.time
        cal.add(Calendar.DAY_OF_WEEK, 3)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        fetchByWeek(startWeek, endWeek)
        return "$startWeek,$endWeek"
    }

    fun getLastWeek(): String {
        cal.time = dateFormat.parse(state["START_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, -7)
        Log.d(TAG, "getLastWeek: ${cal.time}")
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.add(Calendar.DAY_OF_WEEK, 3)
        state["MID_WEEK"] = cal.time.time
        cal.time = dateFormat.parse(state["END_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, -7)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        fetchByWeek(startWeek, endWeek)
        return "$startWeek,$endWeek"
    }

    fun getNextWeek(): String {
        cal.time = dateFormat.parse(state["START_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.add(Calendar.DAY_OF_WEEK, 3)
        state["MID_WEEK"] = cal.time.time
        cal.time = dateFormat.parse(state["END_WEEK"]!!)!!
        cal.add(Calendar.DAY_OF_WEEK, 7)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        fetchByWeek(startWeek, endWeek)
        return "$startWeek,$endWeek"
    }

    fun setChecked(todo: Todo, checked: Boolean) {
        viewModelScope.launch {
            todoRepository.setCheckStatus(todo, checked)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo.todoId!!)
        }
    }

    fun subtaskChecked(todo: Todo, subtask: String, checked: Boolean) {
        viewModelScope.launch {
            todoRepository.setSubTaskCheckStatus(todo, subtask, checked)
        }
    }

    fun getMidWeek(): Long? = state["MID_WEEK"]

    /***********************************************************************************************
     * ************************* UI States
     */
    data class TodosUIState(
        val status: Status = Status.LOADING,
        val todo: Todo? = null,
        val todoList: List<Todo>? = null,
    )
}