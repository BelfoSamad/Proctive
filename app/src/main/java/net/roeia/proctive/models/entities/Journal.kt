package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@Entity
@IgnoreExtraProperties
data class Journal(
    @Exclude @PrimaryKey var journalId: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var isLocked: Boolean = false,
    var createdDate: Date? = null,
    var updatedDate: Date? = null,
)
