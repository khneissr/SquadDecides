package com.example.squaddecides.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.example.squaddecides.model.VoteOption

@Composable
private fun VoterChip(
    voter: String,
    currentUserName: String,
    isWinningOption: Boolean
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isWinningOption) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (isWinningOption) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
            Text(
                text = if (voter == currentUserName) "You" else voter,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isWinningOption) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}

@Composable
private fun VoteOptionHeader(
    option: String,
    voteCount: Int,
    isWinner: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isWinner) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Winner",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = option,
                style = MaterialTheme.typography.titleMedium,
                color = if (isWinner) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Surface(
            shape = CircleShape,
            color = if (isWinner) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            }
        ) {
            Text(
                text = "$voteCount votes",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = if (isWinner) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.secondary
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VotersList(
    voters: List<String>,
    currentUserName: String,
    isWinningOption: Boolean
) {
    if (voters.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Divider(
            color = if (isWinningOption) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            voters.forEach { voter ->
                VoterChip(
                    voter = voter,
                    currentUserName = currentUserName,
                    isWinningOption = isWinningOption
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "No votes yet",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun VoteOptionCard(
    vote: VoteOption,
    isWinner: Boolean,
    voters: List<String>,
    currentUserName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWinner) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            }
        ),
        border = if (isWinner) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            VoteOptionHeader(
                option = vote.option,
                voteCount = vote.count.value,
                isWinner = isWinner
            )

            VotersList(
                voters = voters,
                currentUserName = currentUserName,
                isWinningOption = isWinner
            )
        }
    }
}

@Composable
private fun DialogTitle(totalVotes: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Vote Breakdown",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$totalVotes total votes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun VoteBreakdownDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    votes: List<VoteOption>,
    votersByOption: Map<String, List<String>>,
    currentUserName: String
) {
    if (show) {
        val totalVotes = votes.sumOf { it.count.value }
        val sortedVotes = votes.sortedByDescending { it.count.value }
        val winningVote = sortedVotes.firstOrNull()

        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            title = { DialogTitle(totalVotes) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedVotes) { vote ->
                        VoteOptionCard(
                            vote = vote,
                            isWinner = vote == winningVote,
                            voters = votersByOption[vote.option] ?: emptyList(),
                            currentUserName = currentUserName
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DecisionResultCard(
    votes: List<VoteOption>,
    totalVotes: Int,
    votersByOption: Map<String, List<String>> = emptyMap(),
    currentUserName: String
) {
    var showVoteDetails by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { showVoteDetails = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "View vote details",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Decision Made!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                votes.sortedByDescending { it.count.value }
                    .take(3)
                    .forEachIndexed { index, vote ->
                        val percentage = (vote.count.value.toFloat() / totalVotes * 100).roundToInt()
                        val color = when (index) {
                            0 -> MaterialTheme.colorScheme.primary
                            1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = color,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                    Text(
                                        text = vote.option,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "$percentage%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { percentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = color,
                                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HowToVote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$totalVotes total votes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }

    VoteBreakdownDialog(
        show = showVoteDetails,
        onDismiss = { showVoteDetails = false },
        votes = votes,
        votersByOption = votersByOption,
        currentUserName = currentUserName
    )
}