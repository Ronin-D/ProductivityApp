package com.example.websocket

import android.util.Log
import com.example.network.api.dto.MessageDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val okHttpClient: OkHttpClient,
    private val baseUrl: String
) {

    private var webSocket: WebSocket? = null
    private val _incomingMessages = MutableSharedFlow<MessageDto>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val incomingMessages: SharedFlow<MessageDto> = _incomingMessages

    fun connect(token: String, chatId: String) {
        val request = Request.Builder()
            .url("$baseUrl$chatId/ws")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                val messageDto = try {
                    Gson().fromJson(text, MessageDto::class.java)
                } catch (e: Exception) {
                    Log.e("WebSocketManager", "Failed to parse message: ${e.message}")
                    null
                }
                messageDto?.let {
                    _incomingMessages.tryEmit(it)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closed: $reason")
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "User closed")
        webSocket = null
    }
}
