package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@Entity
@IgnoreExtraProperties
data class Debt(
    @Exclude @PrimaryKey var debtId: Long? = null,
    var amount: Double? = null,
    var amountPaid: Double? = null,
    var debtor: String? = null,
    var debtReverse: Boolean? = null,
    var returnDate: Date? = null,
)
