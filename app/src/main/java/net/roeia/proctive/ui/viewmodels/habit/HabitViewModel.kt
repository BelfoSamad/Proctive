package net.roeia.proctive.ui.viewmodels.habit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.roeia.proctive.data.Status
import net.roeia.proctive.data.repositories.TodoRepository
import net.roeia.proctive.models.entities.todo.Habit
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val todoRepository: TodoRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HabitViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    //UI States
    private val _uiStateHabits = MutableStateFlow(HabitsUIState())
    val uiStateHabits: StateFlow<HabitsUIState> = _uiStateHabits.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchHabits() {
        viewModelScope.launch {
            todoRepository.fetchHabits().collect { habits ->
                _uiStateHabits.update {
                    it.copy(
                        status = Status.SUCCESS,
                        habitsList = habits
                    )
                }
            }
        }
    }

    fun setChecked(habitId: Long, checked: Boolean) {
        //TODO: Set Habit as Checked/UnChecked
    }

    fun saveHabit(habit: Habit) {
        //TODO: Save Habit
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            todoRepository.deleteHabit(habit.habitId!!)
        }
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class HabitsUIState(
        val status: Status = Status.LOADING,
        val habitsList: List<Habit>? = null,
        val checkedHabitsList: List<Habit>? = null,
    )
}