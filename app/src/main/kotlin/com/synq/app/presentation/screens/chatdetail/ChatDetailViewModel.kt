package com.synq.app.presentation.screens.chatdetail
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.synq.app.core.network.TokenManager
import com.synq.app.domain.model.Message
import com.synq.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
data class ChatDetailUiState(val inputText: String = "", val isSending: Boolean = false, val errorMessage: String? = null)
@HiltViewModel class ChatDetailViewModel @Inject constructor(private val chatRepository: ChatRepository, private val tokenManager: TokenManager, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val chatId: String = checkNotNull(savedStateHandle["chatId"])
    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()
    val messages: Flow<PagingData<Message>> = chatRepository.getMessages(chatId).cachedIn(viewModelScope)
    fun getCurrentUserId(): String? = tokenManager.getUserId()
    fun updateInput(text: String) { _uiState.update { it.copy(inputText = text) } }
    fun sendMessage() {
        val content = _uiState.value.inputText.trim()
        if (content.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, inputText = "") }
            chatRepository.sendMessage(chatId, content).onFailure { error -> _uiState.update { it.copy(errorMessage = error.localizedMessage ?: "Failed", inputText = content) } }
            _uiState.update { it.copy(isSending = false) }
        }
    }
}
