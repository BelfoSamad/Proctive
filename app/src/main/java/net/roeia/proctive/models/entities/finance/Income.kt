package net.roeia.proctive.models.entities.finance

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import net.roeia.proctive.models.enums.FinanceType
import java.util.*

@Entity
@IgnoreExtraProperties
data class Income(
    @Exclude @PrimaryKey var incomeId: Long? = null,
    var name: String? = null,
    var amount: Double? = null,
    var isSavings: Boolean? = null,
    var repeatStart: Date? = null,
    var repeatInterval: Long? = null,
    var repeatDayInMonth: Int? = null,
    var percentageSplitting: Map<FinanceType, Float>? = null,
    var amountSplitting: Map<FinanceType, Double>? = null,
    var createdOn: Date? = null
)
