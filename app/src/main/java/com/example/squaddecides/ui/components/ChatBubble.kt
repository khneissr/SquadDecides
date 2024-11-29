package com.example.squaddecides.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatBubble(
    author: String,
    message: String,
    isSentByUser: Boolean,
    showAuthor: Boolean
) {
    val authorColor = when (author) {
        "Mike" -> MaterialTheme.colorScheme.tertiary
        "Jack" -> MaterialTheme.colorScheme.error
        "Ryan" -> MaterialTheme.colorScheme.secondary
        "Aiden" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    val bubbleColor = if (isSentByUser) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.White
    }

    val textColor = if (isSentByUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
    ) {
        if (showAuthor) {
            Text(
                text = if (isSentByUser) "You" else author,
                style = MaterialTheme.typography.labelMedium,
                color = authorColor,
                modifier = Modifier.padding(
                    start = if (isSentByUser) 0.dp else 16.dp,
                    end = if (isSentByUser) 16.dp else 0.dp,
                    bottom = 2.dp
                )
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (isSentByUser) 16.dp else 4.dp,
                topEnd = if (isSentByUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = bubbleColor,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}
