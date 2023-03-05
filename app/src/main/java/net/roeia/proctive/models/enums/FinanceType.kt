package net.roeia.proctive.models.enums

enum class FinanceType(val value: Int) {
    Income(0), Savings(1), Debt(2), Expenses(3), Investments(4), PocketMoney(5);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}