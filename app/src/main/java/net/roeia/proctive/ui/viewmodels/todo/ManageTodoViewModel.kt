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
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
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
        //todoHolder.subTasks = getSubtasksList()
        todoHolder.labels = getLabelsList()

        //TODO (Backend): Save Todo
        todoUpdatedLiveData.value = true
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class TodoUIState(
        val status: Status = Status.LOADING,
        val todo: Todo? = null,
    )
}