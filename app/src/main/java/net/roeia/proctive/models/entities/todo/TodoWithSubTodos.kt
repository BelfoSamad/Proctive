package net.roeia.proctive.models.entities.todo

import androidx.room.Embedded
import androidx.room.Relation

data class TodoWithSubTodos(
    @Embedded
    val todo: Todo,
    @Relation(
        parentColumn = "todoId",
        entityColumn = "parentTodoId"
    )
    val subTodos: List<Todo>
)
