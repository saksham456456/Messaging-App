package com.synq.app.presentation.screens.auth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synq.app.core.result.AppResult
import com.synq.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
data class AuthUiState(val phoneNumber: String = "", val otp: String = "", val isOtpSent: Boolean = false, val isLoading: Boolean = false, val errorMessage: String? = null, val isSuccess: Boolean = false)
@HiltViewModel class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    fun updatePhoneNumber(phoneNumber: String) { _uiState.update { it.copy(phoneNumber = phoneNumber, errorMessage = null) } }
    fun updateOtp(otp: String) { _uiState.update { it.copy(otp = otp, errorMessage = null) } }
    fun requestOtp() {
        val phoneNumber = _uiState.value.phoneNumber
        if (phoneNumber.length < 5) { _uiState.update { it.copy(errorMessage = "Invalid phone number") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.requestOtp(phoneNumber)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isOtpSent = true) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                is AppResult.Loading -> Unit
            }
        }
    }
    fun verifyOtp() {
        val otp = _uiState.value.otp
        if (otp.length < 4) { _uiState.update { it.copy(errorMessage = "Invalid OTP") }; return }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.verifyOtp(_uiState.value.phoneNumber, otp)) {
                is AppResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is AppResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                is AppResult.Loading -> Unit
            }
        }
    }
}
