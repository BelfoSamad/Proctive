package net.roeia.proctive.data.datasources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.roeia.proctive.models.entities.*

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
}