package net.roeia.proctive.data.datasources.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import net.roeia.proctive.models.entities.todo.Habit
import net.roeia.proctive.models.entities.todo.Todo

@Dao
interface HabitsDao {

    @Query("SELECT * FROM Habit WHERE weekDays = :day")//TODO: in Days
    fun fetchHabitsByDay(day: String): Flow<List<Habit>>

    //Delete
    @Query("DELETE FROM Habit WHERE habitId = :habitId")
    suspend fun deleteHabit(habitId: Long)

    //Insert
    @Insert
    suspend fun insertHabit(habit: Habit)

    //Update
    @Update
    suspend fun updateHabit(habit: Habit)
}