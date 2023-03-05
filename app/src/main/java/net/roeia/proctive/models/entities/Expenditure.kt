package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import net.roeia.proctive.models.enums.FinanceType
import java.util.*

@Entity
@IgnoreExtraProperties
data class Expenditure(
    @Exclude @PrimaryKey var expenditureId: Long? = null,
    var name: String? = null,
    var amount: Double? = null,
    var expenditureType: FinanceType? = null,
    var addedOn: Date? = null
)
