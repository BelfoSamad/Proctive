package net.roeia.proctive.ui.viewmodels.startup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.roeia.proctive.usecases.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    val loggedInLiveData: MutableLiveData<Int?> by lazy {
        MutableLiveData<Int?>()
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authUseCase.login(email, password).collect {
                loggedInLiveData.value = it.errorCode
            }
        }
    }
}