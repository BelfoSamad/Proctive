package net.roeia.proctive.data.datasources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.roeia.proctive.data.datasources.local.db.dao.HabitsDao
import net.roeia.proctive.data.datasources.local.db.dao.TodoDao
import net.roeia.proctive.models.entities.finance.Debt
import net.roeia.proctive.models.entities.finance.Expenditure
import net.roeia.proctive.models.entities.finance.Income
import net.roeia.proctive.models.entities.finance.Transfer
import net.roeia.proctive.models.entities.todo.Habit
import net.roeia.proctive.models.entities.todo.Journal
import net.roeia.proctive.models.entities.todo.Todo

@Database(
    entities = [
        Todo::class, Habit::class, Journal::class,
        Income::class, Debt::class, Expenditure::class,
        Transfer::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

    abstract fun getHabitsDao(): HabitsDao

}