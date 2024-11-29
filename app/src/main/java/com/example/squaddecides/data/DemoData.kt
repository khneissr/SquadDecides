package com.example.squaddecides.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.squaddecides.model.Comment
import com.example.squaddecides.model.SystemMessage
import com.example.squaddecides.model.Topic
import com.example.squaddecides.model.VoteOption

object DemoData {
    fun addDemoContent(
        topics: SnapshotStateList<Topic>,
        commentsByTopic: SnapshotStateMap<String, SnapshotStateList<Comment>>,
        votesByTopic: MutableMap<String, SnapshotStateList<VoteOption>>,
        systemMessagesByTopic: SnapshotStateMap<String, SnapshotStateList<SystemMessage>> // New parameter
    ) {
        val demoTopic = Topic(
            id = "example-food",
            title = "boys what we eating?",
            isFinalized = true,
            votingOptions = listOf(
                "option1" to "Chipotle",
                "option2" to "Lazeez",
                "option3" to "Quik Chik"
            )
        )

        val demoSystemMessages = SnapshotStateList<SystemMessage>().apply {
            val baseTime = System.currentTimeMillis() - 3600000
            add(SystemMessage(message = "You have joined the chat", timestamp = baseTime))
            add(SystemMessage(message = "Mike has joined the chat", timestamp = baseTime + 10000))
            add(SystemMessage(message = "Ryan has joined the chat", timestamp = baseTime + 20000))
            add(SystemMessage(message = "Aiden has joined the chat", timestamp = baseTime + 30000))
        }

        val demoVotes = SnapshotStateList<VoteOption>().apply {
            add(VoteOption(
                id = "option1",
                option = "Chipotle",
                count = mutableStateOf(3),
                voters = mutableStateOf(listOf("Mike", "Aiden", "You"))
            ))
            add(VoteOption(
                id = "option2",
                option = "Lazeez",
                count = mutableStateOf(1),
                voters = mutableStateOf(listOf("Jack"))
            ))
            add(VoteOption(
                id = "option3",
                option = "Quik Chik",
                count = mutableStateOf(1),
                voters = mutableStateOf(listOf("Ryan"))
            ))
        }

        val demoComments = SnapshotStateList<Comment>().apply {
            add(Comment("1", "Jack", "yo this project is actually killing me rn need food asap 💀"))
            add(Comment("2", "TestUser", "ong bro haven't eaten since breakfast"))
            add(Comment("3", "Mike", "chipotle? got that rewards points might get us a free bowl"))
            add(Comment("4", "Aiden", "brooo W idea, need them gains anyway 😤"))
            add(Comment("5", "TestUser", "say less bro boutta destroy a double chicken bowl"))
            add(Comment("6", "Ryan", "nah quik chik on deck rn their spicy sandwich different"))
            add(Comment("7", "Jack", "lazeez kinda bussin too ngl"))
            add(Comment("8", "Mike", "plus im driving, chipotle closest"))
            add(Comment("9", "Aiden", "ight everyone vote so we can order"))
            add(Comment("10", "Jack", "voted lazeez on god"))
            add(Comment("11", "Mike", "chipotle all the way 🌯"))
            add(Comment("12", "Ryan", "ight bet just voted quik chik"))
            add(Comment("13", "TestUser", "voted chipotle ✅"))
            add(Comment("14", "Aiden", "chipotle fs, voted lets goooo"))
            add(Comment("15", "TestUser", "chipotle with the dub, massive W"))
            add(Comment("16", "Mike", "bet sending location, whoever wants that free bowl venmo me"))
        }

        if (topics.none { it.id == "example-food" }) {
            topics.add(demoTopic)
            votesByTopic["example-food"] = demoVotes
            commentsByTopic["example-food"] = demoComments
            systemMessagesByTopic["example-food"] = demoSystemMessages
        }
    }
}
