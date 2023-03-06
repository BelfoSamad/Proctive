package net.roeia.proctive.ui.viewmodels.finance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.roeia.proctive.data.Status
import net.roeia.proctive.models.entities.finance.Debt
import net.roeia.proctive.models.entities.finance.Expenditure
import net.roeia.proctive.models.entities.finance.Income
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.models.pojo.Finance
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FinanceViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "FinanceViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    //UI States
    private val _uiStateFinances = MutableStateFlow(FinanceUIState())
    val uiStateFinances: StateFlow<FinanceUIState> = _uiStateFinances.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchFinance(financeType: FinanceType) {
        val expensesList = listOf(
            Expenditure(
                name = "Treats",
                amount = 200.toDouble(),
                addedOn = Date()
            ),
            Expenditure(
                name = "Coffee",
                amount = 150.toDouble(),
                addedOn = Date()
            ),
            Expenditure(
                name = "Taxi",
                amount = 90.toDouble(),
                addedOn = Date()
            )
        )
        viewModelScope.launch {
            when (financeType) {
                FinanceType.Income -> {
                    val incomeList = listOf(
                        Income(
                            name = "Income 1",
                            amount = 10000.toDouble(),
                            repeatStart = Date(),
                            repeatInterval = 1728000000L, //20 days
                            createdOn = Date()
                        ),
                        Income(
                            name = "Income 2",
                            amount = 20000.toDouble(),
                            repeatDayInMonth = 20,
                            createdOn = Date()
                        )
                    )
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = incomeList.map { income ->
                                Finance(
                                    name = income.name!!,
                                    subName = "Created on ${dateFormat.format(income.createdOn!!)}",
                                    details = "${income.amount}da",
                                    subDetails = null
                                )
                            }
                        )
                    }
                }
                FinanceType.Savings -> {
                    val savingsLists = listOf(
                        Income(
                            name = "Bank",
                            amount = 36000.toDouble(),
                        ),
                        Income(
                            name = "CCP",
                            amount = 40000.toDouble(),
                        ),
                    )
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = savingsLists.map { saving ->
                                Finance(
                                    name = saving.name!!,
                                    subName = null,
                                    details = "${saving.amount}da",
                                    subDetails = null
                                )
                            }
                        )
                    }
                }
                FinanceType.Debt -> {
                    val debtList = listOf(
                        Debt(
                            amount = 10000.toDouble(),
                            amountPaid = 5000.toDouble(),
                            debtor = "Mom",
                            debtReverse = false,
                            returnDate = Date()
                        ),
                        Debt(
                            amount = 20000.toDouble(),
                            amountPaid = 10000.toDouble(),
                            debtor = "Dad",
                            debtReverse = false,
                            returnDate = Date()
                        ),
                        Debt(
                            amount = 10000.toDouble(),
                            amountPaid = 5000.toDouble(),
                            debtor = "Mom",
                            debtReverse = true,
                            returnDate = Date()
                        )
                    )
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = debtList.map { debt ->
                                Finance(
                                    name = debt.debtor!!,
                                    subName = "Pay on ${dateFormat.format(debt.returnDate!!)}",
                                    details = "${debt.amount}da",
                                    subDetails = "Paid ${debt.amountPaid}da"
                                )
                            }
                        )
                    }
                }
                FinanceType.Expenses -> {
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = expensesList.map { expense ->
                                Finance(
                                    name = expense.name!!,
                                    subName = dateFormat.format(expense.addedOn!!),
                                    details = "${expense.amount}da",
                                    subDetails = null
                                )
                            }
                        )
                    }
                }
                FinanceType.PocketMoney -> {
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = expensesList.map { expense ->
                                Finance(
                                    name = expense.name!!,
                                    subName = dateFormat.format(expense.addedOn!!),
                                    details = "${expense.amount}da",
                                    subDetails = null
                                )
                            }
                        )
                    }
                }
                FinanceType.Investments -> {
                    _uiStateFinances.update {
                        it.copy(
                            status = Status.SUCCESS,
                            financesList = expensesList.map { expense ->
                                Finance(
                                    name = expense.name!!,
                                    subName = dateFormat.format(expense.addedOn!!),
                                    details = "${expense.amount}da",
                                    subDetails = null
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun getCurrentMonth() = dateFormat.format(Date()).split("/")[1]

    /***********************************************************************************************
     * ************************* UI States
     */
    data class FinanceUIState(
        val status: Status = Status.LOADING,
        val financesList: List<Finance>? = null,
    )
}