package net.roeia.proctive.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import net.roeia.proctive.models.enums.FinanceType

@Entity
@IgnoreExtraProperties
data class Trend(
    @Exclude @PrimaryKey var trendId: Long? = null,
    var type: FinanceType? = null,
    var element: String? = null,
    var lastAmount: Double? = null,
    var changePercentage: Float? = null,
    var isIncreasing: Boolean? = null,
    var intervalUnit: String? = null,
    var interval: Int? = null
)