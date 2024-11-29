package com.example.squaddecides.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squaddecides.model.Topic
import com.example.squaddecides.model.VoteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopicViewModel : ViewModel() {
    private val _topics = MutableStateFlow<List<Topic>>(emptyList())
    val topics: StateFlow<List<Topic>> = _topics

    private val _comments = mutableMapOf<String, MutableStateFlow<List<Pair<String, String>>>>()
    private val _votes = mutableMapOf<String, MutableStateFlow<List<VoteOption>>>()

    fun addTopic(topic: Topic) {
        viewModelScope.launch {
            _topics.value += topic
        }
    }

    fun addComment(topicId: String, author: String, message: String) {
        viewModelScope.launch {
            _comments[topicId]?.value = (_comments[topicId]?.value ?: emptyList()) + (author to message)
        }
    }

    fun updateVote(topicId: String, voteOptionId: String, increment: Boolean) {
        viewModelScope.launch {
            val currentVotes = _votes[topicId]?.value ?: return@launch
            val updatedVotes = currentVotes.map { voteOption ->
                if (voteOption.id == voteOptionId) {
                    voteOption.copy(
                        count = mutableStateOf(
                            if (increment) voteOption.count.value + 1
                            else maxOf(0, voteOption.count.value - 1)
                        )
                    )
                } else voteOption
            }
            _votes[topicId]?.value = updatedVotes
        }
    }
}
