package net.roeia.proctive.data.datasources.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.roeia.proctive.models.enums.FinanceType
import java.lang.reflect.Type
import java.util.*

class DataConverter {
    private val gson: Gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringToList(value: String?): List<String>? {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMap(value: String?): Map<String, Boolean>? {
        val listType: Type = object : TypeToken<Map<String, Boolean>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun mapToString(list: Map<String, Boolean>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMapPercentage(value: String?): Map<FinanceType, Float>? {
        val listType: Type = object : TypeToken<Map<FinanceType, Float>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun percentageMapToString(list: Map<FinanceType, Float>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToMapAmount(value: String?): Map<FinanceType, Double>? {
        val listType: Type = object : TypeToken<Map<FinanceType, Float>?>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun amountMapToString(list: Map<FinanceType, Double>?): String? {
        return gson.toJson(list)
    }
}