package com.mytask.core.data.chat

import kotlinx.coroutines.flow.Flow

/**
 * AI Chat Service - Interface for pluggable AI chat providers.
 *
 * Implement this interface to connect to any AI provider (OpenAI, Anthropic, Gemini, etc.).
 *
 * Usage:
 * ```
 * class OpenAiChatService(private val client: HttpClient) : AiChatService {
 *     override suspend fun sendMessage(message: String, conversationId: String?): AiChatResponse {
 *         val response = client.post("https://api.openai.com/v1/chat/completions") { ... }
 *         return AiChatResponse(message = response.choices.first().message.content)
 *     }
 *
 *     override fun sendMessageStream(message: String, conversationId: String?): Flow<String> {
 *         return flow { /* SSE streaming implementation */ }
 *     }
 *
 *     override suspend fun clearConversation(conversationId: String) { /* clear context */ }
 * }
 * ```
 */
interface AiChatService {
    suspend fun sendMessage(
        message: String,
        conversationId: String? = null
    ): AiChatResponse

    fun sendMessageStream(
        message: String,
        conversationId: String? = null
    ): Flow<String>

    suspend fun clearConversation(conversationId: String)
}

data class AiChatResponse(
    val message: String,
    val conversationId: String? = null,
    val metadata: Map<String, String> = emptyMap()
)
