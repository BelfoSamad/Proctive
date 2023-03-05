package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import net.roeia.proctive.models.enums.FinanceType
import java.util.*

@Entity
@IgnoreExtraProperties
data class Transfer(
    @Exclude @PrimaryKey var transferId: Long? = null,
    var senderType: FinanceType? = null,
    var receiverType: FinanceType? = null,
    var amount: Double? = null,
    var nextTransfer: Date? = null
)
