package net.roeia.proctive.ui.viewmodels.habit

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
import net.roeia.proctive.models.entities.Habit
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.ui.viewmodels.todo.TodoViewModel
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
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
    fun fetchHabits(){
        //TODO: Get Habits List based on day (Checked and UnChecked) + Sort it based on position
        viewModelScope.launch {
            _uiStateHabits.update {
                it.copy(
                    status = Status.SUCCESS,
                    habitsList = listOf(
                        Habit(
                            habitId = 1L,
                            name = "Morning Athkars",
                            time = "12:00",
                            weekDays = listOf("Sun", "Mon"),
                            isChecked = false
                        ),
                        Habit(
                            habitId = 1L,
                            name = "Evening Athkars",
                            time = "12:00",
                            weekDays = listOf("Sun", "Mon"),
                            isChecked = false
                        )
                    ),
                    checkedHabitsList = listOf(
                        Habit(
                            habitId = 1L,
                            name = "Morning Athkars",
                            time = "12:00",
                            weekDays = listOf("Sun", "Mon"),
                            isChecked = true
                        ),
                        Habit(
                            habitId = 1L,
                            name = "Evening Athkars",
                            time = "12:00",
                            weekDays = listOf("Sun", "Mon"),
                            isChecked = true
                        )
                    )
                )
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
        //TODO: Delete Habit
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