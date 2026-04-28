package com.synq.app.presentation.screens.newchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.synq.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewChatUiState(
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newlyCreatedChatId: String? = null
)

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewChatUiState())
    val uiState: StateFlow<NewChatUiState> = _uiState.asStateFlow()

    fun updatePhoneNumber(phone: String) {
        _uiState.update { it.copy(phoneNumber = phone, errorMessage = null) }
    }

    fun startChat() {
        val phone = _uiState.value.phoneNumber.trim()
        if (phone.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid phone number") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = chatRepository.createChat(phone)
            result.onSuccess { chatId ->
                _uiState.update { it.copy(isLoading = false, newlyCreatedChatId = chatId) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage) }
            }
        }
    }
}
