package com.synq.app.presentation.screens.chatlist
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.synq.app.domain.model.Chat
import com.synq.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel class ChatListViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    val chats: Flow<PagingData<Chat>> = chatRepository.getChats().cachedIn(viewModelScope)
    init { viewModelScope.launch { chatRepository.syncChats() } }
}
