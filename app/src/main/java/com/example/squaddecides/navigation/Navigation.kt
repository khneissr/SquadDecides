package com.example.squaddecides.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.squaddecides.data.DemoData
import com.example.squaddecides.ui.screens.*
import com.example.squaddecides.model.*

@Composable
fun AppNavigation(navController: NavHostController) {
    val topics = remember { mutableStateListOf<Topic>() }
    val commentsByTopic = remember {
        mutableStateMapOf<String, SnapshotStateList<Comment>>()
    }
    val systemMessagesByTopic = remember {
        mutableStateMapOf<String, SnapshotStateList<SystemMessage>>()
    }
    val userVotesByTopic = remember {
        mutableStateMapOf<String, MutableState<String?>>()
    }
    val votesByTopic = remember {
        mutableStateMapOf<String, SnapshotStateList<VoteOption>>()
    }
    val votersByTopic = remember {
        mutableStateMapOf<String, MutableMap<String, List<String>>>()
    }
    var userName by remember { mutableStateOf("") }

    DemoData.addDemoContent(topics, commentsByTopic, votesByTopic, systemMessagesByTopic)

    NavHost(
        navController = navController,
        startDestination = "signin"
    ) {

        composable("signin") {
            SignInScreen(
                navController = navController,
                onSignIn = { newUsername -> userName = newUsername }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                topics = topics,
                userName = userName,
                onNameChange = { newName -> userName = newName }
            )
        }

        composable("createTopic") {
            CreateTopicScreen(
                navController = navController,
                topics = topics
            )
        }

        composable("discussion/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topic = topics.find { it.id == topicId }

            topic?.let {
                val comments = commentsByTopic.getOrPut(topicId) { mutableStateListOf() }
                val systemMessages = systemMessagesByTopic.getOrPut(topicId) { mutableStateListOf() }
                val votes = votesByTopic.getOrPut(topicId) {
                    mutableStateListOf<VoteOption>().apply {
                        topic.votingOptions.forEach { (id, optionText) ->
                            add(VoteOption(
                                id = id,
                                option = optionText,
                                count = mutableStateOf(0),
                                voters = mutableStateOf(emptyList())
                            ))
                        }
                    }
                }

                val votersMap = votersByTopic.getOrPut(topicId) { mutableMapOf() }
                val votersByOption = votes.associate { vote ->
                    vote.option to (vote.voters.value)
                }

                DiscussionScreen(
                    topicId = topicId,
                    topic = topic,
                    navController = navController,
                    comments = comments,
                    systemMessages = systemMessages,
                    topics = topics,
                    userName = userName,
                    votes = votes,
                    votersByOption = votersByOption
                )
            }
        }

        composable("voting/{topicId}") { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            val topic = topics.find { it.id == topicId }

            topic?.let {
                val userVote = userVotesByTopic.getOrPut(topicId) { mutableStateOf<String?>(null) }
                val votes = votesByTopic.getOrPut(topicId) {
                    mutableStateListOf<VoteOption>().apply {
                        topic.votingOptions.forEach { (id, optionText) ->
                            add(VoteOption(
                                id = id,
                                option = optionText,
                                count = mutableStateOf(0),
                                voters = mutableStateOf(emptyList())
                            ))
                        }
                    }
                }

                VotingScreen(
                    topicId = topicId,
                    topic = topic,
                    navController = navController,
                    votes = votes,
                    votingOptions = topic.votingOptions,
                    isFinalized = topic.isFinalized,
                    userVote = userVote,
                    onVoteChange = { newVote ->
                        userVote.value = newVote
                        userVotesByTopic[topicId]?.value = newVote
                    },
                    userName = userName
                )
            }
        }
    }
}
