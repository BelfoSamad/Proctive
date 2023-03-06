package net.roeia.proctive.ui.viewmodels.startup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.roeia.proctive.usecases.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {
    companion object {
        private const val TAG = "RegisterViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    val registeredLiveData: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun registerUser(username: String, email: String, password: String) {
        viewModelScope.launch {
            authUseCase.register(username, email, password).collect {
                registeredLiveData.value = it.errorCode
            }
        }
    }
}