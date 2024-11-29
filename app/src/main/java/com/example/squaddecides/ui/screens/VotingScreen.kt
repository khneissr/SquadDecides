package com.example.squaddecides.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.squaddecides.model.Topic
import com.example.squaddecides.model.VoteOption
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check

@Composable
fun VotingScreen(
    topicId: String,
    topic: Topic,
    navController: NavController,
    votes: SnapshotStateList<VoteOption>,
    votingOptions: List<Pair<String, String>>,
    isFinalized: Boolean,
    userVote: MutableState<String?>,
    onVoteChange: (String?) -> Unit,
    userName: String
) {
    val totalVotes = votes.sumOf { it.count.value }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Vote",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = votes.toList(),
                key = { it.id }
            ) { voteOption ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = voteOption.option,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${voteOption.count.value} votes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FilledTonalButton(
                            onClick = {
                                if (!isFinalized) {
                                    if (userVote.value == voteOption.id) {
                                        voteOption.count.value = maxOf(0, voteOption.count.value - 1)
                                        voteOption.voters.value = voteOption.voters.value - userName
                                        onVoteChange(null)
                                    } else {
                                        if (userVote.value != null) {
                                            votes.find { it.id == userVote.value }?.let { previousVote ->
                                                previousVote.count.value = maxOf(0, previousVote.count.value - 1)
                                                previousVote.voters.value = previousVote.voters.value - userName
                                            }
                                        }
                                        voteOption.count.value += 1
                                        voteOption.voters.value = voteOption.voters.value + userName
                                        onVoteChange(voteOption.id)
                                    }
                                }
                            },
                            enabled = !isFinalized,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (userVote.value == voteOption.id)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.secondaryContainer
                            ),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            if (userVote.value == voteOption.id) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Voted",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    "Vote",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (isFinalized) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Voting is closed for this discussion",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}