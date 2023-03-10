package net.roeia.proctive.data.datasources.local.db.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.entities.todo.TodoWithSubTodos
import net.roeia.proctive.models.enums.TodoType

@Dao
interface TodoDao {

    //Queries
    @Query("SELECT * FROM Todo WHERE type = :todoType")
    fun getTodoByType(todoType: TodoType) : Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE type = :type AND due BETWEEN :dayStart AND :dayEnd")
    fun getTodoByDay(type: TodoType, dayStart: Long, dayEnd: Long): Flow<List<Todo>>

    @Query("SELECT * FROM Todo WHERE todoId = :todoId")
    fun getTodoById(todoId: Long): Flow<TodoWithSubTodos>

    //Delete
    @Query("DELETE FROM Todo WHERE todoId = :todoId")
    suspend fun deleteTodo(todoId: Long)

    //Insert
    @Insert
    suspend fun insertTodo(todo: Todo)

    //Update
    @Update
    suspend fun updateTodo(todo: Todo)
}