package net.roeia.proctive.data.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import net.roeia.proctive.data.datasources.local.db.dao.HabitsDao
import net.roeia.proctive.data.datasources.local.db.dao.TodoDao
import net.roeia.proctive.models.entities.todo.Habit
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import java.util.*

class TodoRepository(private val todoDao: TodoDao, private val habitsDao: HabitsDao) {
    private val cal: Calendar = Calendar.getInstance()
    private val days = listOf("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat")

    fun fetchTodos(todoType: TodoType): Flow<List<Todo>> = flow {
        when (todoType) {
            TodoType.Goal, TodoType.SubGoal -> {
                todoDao.getTodoByType(todoType).collect { emit(it) }
            }
            TodoType.WeeklyGoal -> {
                //Do Nothing
            }
            TodoType.Task -> {
                cal.time = Date()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                val startTime = cal.time.time
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val endTime = cal.time.time
                todoDao.getTodoByDay(TodoType.Task, startTime, endTime).collect { emit(it) }
            }
        }
    }

    fun fetchTodoById(todoId: Long): Flow<Todo> = flow {
        todoDao.getTodoById(todoId).collect {
            val todo = it.todo
            todo.subGoals = it.subTodos
            emit(todo)
        }
    }

    fun fetchTodosByWeek(start: Date?, end: Date?): Flow<List<Todo>> = flow {
        todoDao.getTodoByDay(TodoType.WeeklyGoal, start!!.time, end!!.time).collect {
            emit(it)
        }
    }

    fun fetchHabits(): Flow<List<Habit>> = flow {
        //TODO (Backend): Set first day based on user's preference (Default: Sun)
        //TODO: Get Habits List based on day (Checked and UnChecked) + Sort it based on position
        habitsDao.fetchHabitsByDay(days[cal.get(Calendar.DAY_OF_WEEK) - 1]).collect {
            emit(it)
        }
    }

    suspend fun setCheckStatus(todo: Todo, checked: Boolean) {
        todo.isChecked = checked
        todoDao.updateTodo(todo)
    }

    suspend fun setSubTaskCheckStatus(todo: Todo, subtask: String, checked: Boolean) {
        todo.subTasks!![subtask] = checked
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodo(todoId: Long) {
        todoDao.deleteTodo(todoId)
    }

    suspend fun deleteHabit(habitId: Long) {
        habitsDao.deleteHabit(habitId)
    }

    suspend fun saveTodo(todo: Todo) {
        if (todo.todoId == null)
            todoDao.insertTodo(todo)
        else todoDao.updateTodo(todo)
    }

    suspend fun saveHabit(habit: Habit) {
        if (habit.habitId == null)
            habitsDao.insertHabit(habit)
        else habitsDao.updateHabit(habit)
    }

}