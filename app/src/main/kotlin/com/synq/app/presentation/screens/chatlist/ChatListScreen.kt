package com.synq.app.presentation.screens.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.synq.app.domain.model.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    val chatsPagingItems = viewModel.chats.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Synq", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewChatClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Filled.Add, contentDescription = "New Chat")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(count = chatsPagingItems.itemCount, key = { index -> chatsPagingItems[index]?.id ?: index }) { index ->
                    chatsPagingItems[index]?.let { chat -> ChatItem(chat = chat, onClick = { onChatClick(chat.id) }) }
                }
                chatsPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> item { LoadingItem() }
                        loadState.append is LoadState.Loading -> item { LoadingItem() }
                        loadState.refresh is LoadState.Error -> item { ErrorItem((loadState.refresh as LoadState.Error).error.localizedMessage) }
                        loadState.append is LoadState.Error -> item { ErrorItem((loadState.append as LoadState.Error).error.localizedMessage) }
                        itemCount == 0 && loadState.refresh is LoadState.NotLoading -> item { EmptyChatsItem() }
                    }
                }
            }
        }
    }
}

@Composable fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) { Text(text = chat.name.firstOrNull()?.uppercase() ?: "?", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = chat.lastMessage?.content ?: "No messages yet", maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        if (chat.unreadCount > 0) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) { Text(text = chat.unreadCount.toString(), color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
@Composable fun LoadingItem() { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) } }
@Composable fun ErrorItem(message: String?) { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { Text(text = "Error: " + (message ?: "Unknown"), color = MaterialTheme.colorScheme.error) } }
@Composable fun EmptyChatsItem() { Box(modifier = Modifier.fillMaxWidth().fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("No chats yet", style = MaterialTheme.typography.titleLarge); Text("Start a conversation", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
