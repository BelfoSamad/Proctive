package net.roeia.proctive.ui.viewmodels.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.roeia.proctive.usecases.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val authUseCase: AuthUseCase) : ViewModel() {
    companion object {
        private const val TAG = "ProfileViewModel"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */

    /***********************************************************************************************
     * ************************* Methods
     */
    fun logout() {
        authUseCase.logout()
    }
}