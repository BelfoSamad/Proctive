package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import net.roeia.proctive.models.enums.TodoType
import java.util.*

@Entity
@IgnoreExtraProperties
data class Todo(
    @Exclude @PrimaryKey var todoId: Long? = null,
    var parentTodoId: Long? = null,
    var name: String? = null,
    var labels: List<String>? = null,
    var description: String? = null,
    var type: TodoType? = null,
    var isChecked: Boolean = false,
    var goalRef: Long? = null,
    var due: Date? = null,
    var pomodoroAverage: Int? = null,
    var subTasks: Map<String, Boolean>? = null,
    @Exclude @Ignore var subGoals: List<Todo>? = null
)
