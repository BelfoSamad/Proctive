package net.roeia.proctive.models.enums

enum class TodoType(val value: Int) {
    Goal(0), SubGoal(1), WeeklyGoal(2), Task(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}