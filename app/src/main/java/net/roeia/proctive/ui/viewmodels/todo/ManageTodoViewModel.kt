package net.roeia.proctive.ui.viewmodels.todo

import androidx.lifecycle.MutableLiveData
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
class ManageTodoViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "ManageTodoViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val dateFormat = SimpleDateFormat("dd-MM", Locale.ROOT)
    private val cal: Calendar = Calendar.getInstance()

    //UI States
    private val _uiStateTodo = MutableStateFlow(TodoUIState())
    val uiStateTodo: StateFlow<TodoUIState> = _uiStateTodo.asStateFlow()

    private var todoHolder = Todo()

    //LiveData
    val todoUpdatedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchTodoById(todoId: Long) {
        viewModelScope.launch {
            _uiStateTodo.update {
                //TODO: (Backend) Get Todo from Database -> Set it to holder + Get Lists separately
                it.copy(
                    status = Status.SUCCESS,
                    todo = Todo()
                )
            }
        }
    }

    fun setLabelsList(labels: List<String>) {
        state["LABELS"] = labels
    }

    fun setSubtasksList(subtasks: List<String>) {
        state["SUBTASKS"] = subtasks
    }

    fun getLabelsList(): List<String>? = state["LABELS"]

    fun getSubtasksList(): List<String>? = state["SUBTASKS"]

    fun deleteTodo(todoId: Long) {
        //TODO (Backend): Delete Todo
        todoUpdatedLiveData.value = true
    }

    fun saveTodo(
        type: Int,
        name: String,
        description: String?,
        due: Date?
    ) {
        todoHolder.type = TodoType.fromInt(type)
        todoHolder.name = name
        todoHolder.description = description
        todoHolder.due = due
        todoHolder.subTasks = getSubtasksList()?.associateWith { false }
        todoHolder.labels = getLabelsList()

        //TODO (Backend): Save Todo
        todoUpdatedLiveData.value = true
    }

    fun getCurrentWeek(weekDate: Date?): String {
        //TODO (Backend): Set first day based on user's preference (Default: Sun)
        if (weekDate != null)
            cal.time = weekDate
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startWeek = dateFormat.format(cal.time)
        state["START_WEEK"] = startWeek
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val endWeek = dateFormat.format(cal.time)
        state["END_WEEK"] = endWeek
        return "$startWeek -> $endWeek"
    }

    fun removeSubtask(subtask: String) {
        val subtasks: MutableList<String> = state.get<List<String>>("SUBTASKS")!!.toMutableList()
        subtasks.remove(subtask)
        state["SUBTASKS"] = subtasks
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class TodoUIState(
        val status: Status = Status.LOADING,
        val todo: Todo? = null,
    )
}