package com.example.squaddecides.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class VoteOption(
    val id: String,
    val option: String,
    val count: MutableState<Int>,
    val voters: MutableState<List<String>> = mutableStateOf(emptyList())
)

data class Topic(
    val id: String,
    val title: String,
    val isFinalized: Boolean,
    val votingOptions: List<Pair<String, String>>
)

data class Comment(
    val id: String,
    val author: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

sealed class ChatMessage {
    abstract val id: String
    abstract val timestamp: Long
}

data class UserMessage(
    override val id: String,
    val author: String,
    val message: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ChatMessage()

data class SystemMessage(
    override val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    override val timestamp: Long = System.currentTimeMillis()
) : ChatMessage()