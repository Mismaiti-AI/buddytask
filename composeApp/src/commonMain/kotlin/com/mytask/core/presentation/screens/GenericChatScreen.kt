package com.mytask.core.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Generic Chat Screen - Pre-Built Template Component
 *
 * Chat UI with message bubbles, input bar, typing indicator, and date grouping.
 * Works with ANY data type through generics.
 *
 * Usage:
 * ```
 * GenericChatScreen(
 *     title = "Support Chat",
 *     messages = chatMessages,
 *     onSendMessage = { text -> viewModel.sendMessage(text) },
 *     isTyping = uiState.isTyping,
 *     onBackClick = { navController.popBackStack() }
 * ) { message ->
 *     // Optional: custom bubble content
 *     Text(message.text)
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericChatScreen(
    title: String,
    messages: List<ChatMessage<T>>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    isTyping: Boolean = false,
    emptyMessage: String = "No messages yet",
    inputPlaceholder: String = "Type a message...",
    dateHeaderProvider: ((ChatMessage<T>) -> String?)? = null,
    bubbleContent: (@Composable (ChatMessage<T>) -> Unit)? = null
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText.trim())
                        inputText = ""
                    }
                },
                placeholder = inputPlaceholder
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }

                    items(messages) { message ->
                        val dateHeader = dateHeaderProvider?.invoke(message)
                        if (dateHeader != null) {
                            DateHeader(dateHeader)
                        }
                        ChatBubble(
                            message = message,
                            bubbleContent = bubbleContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> ChatBubble(
    message: ChatMessage<T>,
    bubbleContent: (@Composable (ChatMessage<T>) -> Unit)?
) {
    val isEnd = message.alignment == ChatBubbleAlignment.END
    val bubbleColor = if (isEnd) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isEnd) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isEnd) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isEnd) 16.dp else 4.dp,
                bottomEnd = if (isEnd) 4.dp else 16.dp
            ),
            color = bubbleColor
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (bubbleContent != null) {
                    bubbleContent(message)
                } else {
                    Text(
                        text = message.content.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    val transition = rememberInfiniteTransition()
    val dot1Alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dot2Alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val dot3Alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(dot1Alpha, dot2Alpha, dot3Alpha).forEach { alpha ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String
) {
    Surface(
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class ChatMessage<T>(
    val id: String,
    val content: T,
    val alignment: ChatBubbleAlignment,
    val timestamp: Long
)

enum class ChatBubbleAlignment {
    START,
    END
}
