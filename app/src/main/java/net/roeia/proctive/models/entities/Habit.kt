package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@Entity
@IgnoreExtraProperties
data class Habit(
    @Exclude @PrimaryKey var habitId: Long? = null,
    var name: String? = null,
    var weekDays: List<String>? = null,
    var time: String? = null,
    var position: Int? = null,
    @Exclude @Ignore var isChecked: Boolean? = null
)