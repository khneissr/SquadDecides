package com.example.squaddecides.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.squaddecides.model.ChatMessage
import com.example.squaddecides.model.Comment
import com.example.squaddecides.model.SystemMessage
import com.example.squaddecides.model.Topic
import com.example.squaddecides.model.UserMessage
import com.example.squaddecides.model.VoteOption
import com.example.squaddecides.ui.components.ChatBubble
import com.example.squaddecides.ui.components.DecisionResultCard
import com.example.squaddecides.ui.theme.ChatBackground

@Composable
private fun InviteDialog(
    showDialog: Boolean,
    username: String,
    onUsernameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
            },
            title = {
                Text("Invite Someone")
            },
            text = {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    enabled = username.isNotBlank()
                ) {
                    Text("Invite")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun InviteSuccessSnackbar(
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (show) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = onDismiss) {
                    Text("DISMISS", color = MaterialTheme.colorScheme.inversePrimary)
                }
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.inversePrimary
                )
                Text("Invitation sent!")
            }
        }
    }
}

@Composable
private fun TopBar(
    topic: Topic,
    navController: NavController,
    onShareClick: () -> Unit,
    onFinalizeClick: () -> Unit,
    votes: List<VoteOption>,
    topicId: String
) {
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

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (topic.isFinalized) "Decision Finalized" else "${topic.votingOptions.size} options available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            if (!topic.isFinalized) {
                FilledTonalIconButton(
                    onClick = onShareClick,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            if (!topic.isFinalized) {
                val totalVotes = votes.sumOf { it.count.value }
                IconButton(
                    onClick = onFinalizeClick,
                    enabled = totalVotes > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Finalize Decision",
                        tint = if (totalVotes > 0)
                            MaterialTheme.colorScheme.onBackground
                        else
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(
                onClick = { navController.navigate("voting/$topicId") },
                enabled = !topic.isFinalized
            ) {
                Icon(
                    imageVector = Icons.Default.HowToVote,
                    contentDescription = "Go to Voting",
                    tint = if (!topic.isFinalized)
                        MaterialTheme.colorScheme.onBackground
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun SystemMessageItem(message: SystemMessage) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInput(
    commentText: String,
    onCommentChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = commentText,
                onValueChange = onCommentChange,
                placeholder = { Text("Type a message") },
                modifier = Modifier
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(24.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendMessage() }),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = onSendMessage,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun MessagesList(
    messages: List<ChatMessage>,
    userName: String,
    listState: LazyListState,
    topic: Topic,
    winningVote: VoteOption?,
    votes: List<VoteOption>,
    votersByOption: Map<String, List<String>>
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = messages.sortedBy { it.timestamp },
            key = { message -> message.id }
        ) { message ->
            when (message) {
                is UserMessage -> {
                    ChatBubble(
                        author = message.author,
                        message = message.message,
                        isSentByUser = message.author == userName,
                        showAuthor = true
                    )
                }
                is SystemMessage -> {
                    SystemMessageItem(message)
                }
            }
        }

        if (topic.isFinalized && winningVote != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                DecisionResultCard(
                    votes = votes,
                    totalVotes = votes.sumOf { it.count.value },
                    votersByOption = votersByOption,
                    userName
                )
            }
        }
    }
}

@Composable
fun DiscussionScreen(
    topicId: String,
    topic: Topic,
    navController: NavController,
    comments: SnapshotStateList<Comment>,
    systemMessages: SnapshotStateList<SystemMessage>,
    topics: SnapshotStateList<Topic>,
    userName: String,
    votes: SnapshotStateList<VoteOption>,
    votersByOption: Map<String, List<String>>
) {
    var commentText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var shouldScrollToLast by remember { mutableStateOf(true) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteUsername by remember { mutableStateOf("") }
    var showInviteSuccess by remember { mutableStateOf(false) }

    val allMessages by remember(comments, systemMessages) {
        derivedStateOf {
            (comments.map { comment ->
                UserMessage(comment.id, comment.author, comment.message, comment.timestamp)
            } + systemMessages).sortedBy { it.timestamp }
        }
    }

    val winningVote = remember(topic.isFinalized, votes) {
        if (topic.isFinalized) {
            votes.maxByOrNull { it.count.value }
        } else null
    }

    LaunchedEffect(allMessages.size, shouldScrollToLast) {
        if (shouldScrollToLast) {
            listState.animateScrollToItem(allMessages.size)
            shouldScrollToLast = false
        }
    }

    InviteDialog(
        showDialog = showInviteDialog,
        username = inviteUsername,
        onUsernameChange = { inviteUsername = it },
        onDismiss = { showInviteDialog = false },
        onConfirm = {
            if (inviteUsername.isNotBlank()) {
                systemMessages.add(
                    SystemMessage(message = "$inviteUsername has joined the chat")
                )
                showInviteDialog = false
                showInviteSuccess = true
                shouldScrollToLast = true
                inviteUsername = ""
            }
        }
    )

    InviteSuccessSnackbar(
        show = showInviteSuccess,
        onDismiss = { showInviteSuccess = false }
    )

    Scaffold(
        topBar = {
            TopBar(
                topic = topic,
                navController = navController,
                onShareClick = { showInviteDialog = true },
                onFinalizeClick = {
                    val index = topics.indexOfFirst { it.id == topicId }
                    if (index != -1) {
                        topics[index] = topics[index].copy(isFinalized = true)
                    }
                },
                votes = votes,
                topicId = topicId
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ChatBackground)
                .padding(paddingValues)
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                MessagesList(
                    messages = allMessages,
                    userName = userName,
                    listState = listState,
                    topic = topic,
                    winningVote = winningVote,
                    votes = votes,
                    votersByOption = votersByOption
                )
            }

            if (!topic.isFinalized) {
                ChatInput(
                    commentText = commentText,
                    onCommentChange = { commentText = it },
                    onSendMessage = {
                        if (commentText.isNotBlank()) {
                            val newComment = Comment(
                                id = java.util.UUID.randomUUID().toString(),
                                author = userName,
                                message = commentText
                            )
                            comments.add(newComment)
                            commentText = ""
                            shouldScrollToLast = true
                        }
                    }
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Text(
                        text = "Discussion is closed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

