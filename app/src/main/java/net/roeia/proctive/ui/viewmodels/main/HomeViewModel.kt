package net.roeia.proctive.ui.viewmodels.main

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
import net.roeia.proctive.models.entities.todo.Journal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    //UI States
    private val _uiStateJournal = MutableStateFlow(JournalUIState())
    val uiStateJournal: StateFlow<JournalUIState> = _uiStateJournal.asStateFlow()

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchJournal(){
        viewModelScope.launch {
            _uiStateJournal.update {
                it.copy(
                    status = Status.SUCCESS,
                    journalList = listOf(
                        Journal(
                            journalId = 1L,
                            title = "My Journal Note Name",
                            content = "This is the text of the journal, please read properly if you want to have an idea about how the description might look like",
                            createdDate = Date()
                        ),
                        Journal(
                            journalId = 1L,
                            title = "My Journal Note Name",
                            content = "This is the text of the journal, please read properly if you want to have an idea about. if you want to have an idea about how the description might look like",
                            createdDate = Date()
                        )
                    )
                )
            }
        }
    }

    fun lockJournal(journal: Journal) {
        //TODO: Lock Journal
    }

    /***********************************************************************************************
     * ************************* UI States
     */
    data class JournalUIState(
        val status: Status = Status.LOADING,
        val journalList: List<Journal>? = null,
    )
}