package net.roeia.proctive.ui.viewmodels.main

import androidx.lifecycle.MutableLiveData
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "JournalViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val cal: Calendar = Calendar.getInstance()

    //UI States
    private val _uiStateJournal = MutableStateFlow(JournalUIState())
    val uiStateJournal: StateFlow<JournalUIState> = _uiStateJournal.asStateFlow()

    //LiveData
    val journalDeletedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val journalUpdatedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun fetchJournalById(journalId: Long) {
        viewModelScope.launch {
            state["TITLE"] = "My Journal Note Name"
            state["CONTENT"] =
                "This is the text of the journal, please read properly if you want to have an idea about. if you want to have an idea about how the description might look like"
            _uiStateJournal.update {
                it.copy(
                    status = Status.SUCCESS,
                    journal = Journal(
                        title = "My Journal Note Name",
                        content = "This is the text of the journal, please read properly if you want to have an idea about. if you want to have an idea about how the description might look like",
                        createdDate = Date()
                    )
                )
            }
        }
    }

    fun getDateFrom(lastUpdate: Date): String {
        return "${TimeUnit.MILLISECONDS.toDays(Date().time - lastUpdate.time) / 356}:" +
                "${TimeUnit.MILLISECONDS.toDays(Date().time - lastUpdate.time) / 30}:" +
                "${TimeUnit.MILLISECONDS.toDays(Date().time - lastUpdate.time)}:" +
                "${TimeUnit.MILLISECONDS.toHours(Date().time - lastUpdate.time)}:" +
                "${TimeUnit.MILLISECONDS.toMinutes(Date().time - lastUpdate.time)}"
    }

    fun deleteJournal() {
        //TODO: Delete Journal
        journalDeletedLiveData.value = true
    }

    fun addOrUpdate(title: String, content: String) {
        state["TITLE"] = title
        state["CONTENT"] = content
        journalUpdatedLiveData.value = true
    }

    fun getCurrentTitle(): String? = state["TITLE"]

    fun getCurrentContent(): String? = state["CONTENT"]

    /***********************************************************************************************
     * ************************* UI States
     */
    data class JournalUIState(
        val status: Status = Status.LOADING,
        val journal: Journal? = null,
    )
}