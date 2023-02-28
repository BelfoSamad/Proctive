package net.roeia.proctive.data.datasources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.roeia.proctive.models.entities.Todo

@Database(
    entities = [Todo::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase()  {
}