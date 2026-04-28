package com.synq.app.presentation.screens.chatdetail
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.synq.app.domain.model.Message
import com.synq.app.domain.model.MessageStatus
import com.synq.app.core.theme.BubbleReceived
import com.synq.app.core.theme.BubbleSent
import com.synq.app.core.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ChatDetailScreen(chatId: String, onBack: () -> Unit, viewModel: ChatDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()
    val messages = viewModel.messages.collectAsLazyPagingItems()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Chat", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface)) },
        bottomBar = { ChatInputBar(inputText = uiState.inputText, onInputChanged = viewModel::updateInput, onSend = viewModel::sendMessage, isSending = uiState.isSending) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize(), reverseLayout = true, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(count = messages.itemCount, key = { index -> messages[index]?.id ?: index }) { index ->
                    messages[index]?.let { message -> MessageBubble(message = message, isOwnMessage = message.senderId == currentUserId) }
                }
            }
        }
    }
}

@Composable fun MessageBubble(message: Message, isOwnMessage: Boolean) {
    val alignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isOwnMessage) BubbleSent else BubbleReceived
    val shape = if (isOwnMessage) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(message.createdAt))

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), contentAlignment = alignment) {
        Column(modifier = Modifier.widthIn(max = 280.dp).clip(shape).background(bubbleColor).padding(12.dp)) {
            Text(text = message.content, color = Color.White)
            Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                Text(text = formattedTime, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                if (isOwnMessage) { Spacer(modifier = Modifier.width(4.dp)); Text(text = when(message.status) { MessageStatus.PENDING -> "⏳"; MessageStatus.SENT -> "✓"; MessageStatus.DELIVERED -> "✓✓"; MessageStatus.READ -> "👀"; MessageStatus.FAILED -> "❌"; else -> "" }, style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}

@Composable fun ChatInputBar(inputText: String, onInputChanged: (String) -> Unit, onSend: () -> Unit, isSending: Boolean) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(value = inputText, onValueChange = onInputChanged, modifier = Modifier.weight(1f).clip(RoundedCornerShape(24.dp)), placeholder = { Text("Message") }, colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent), maxLines = 4)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSend, enabled = inputText.isNotBlank() && !isSending, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(24.dp)).background(TealAccent)) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White) }
        }
    }
}
