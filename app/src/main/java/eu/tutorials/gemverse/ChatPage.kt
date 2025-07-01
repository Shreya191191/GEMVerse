package eu.tutorials.gemverse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.GEMVerse.ui.theme.ColorModalMessage
import eu.tutorials.GEMVerse.ui.theme.ColorUserMessage

import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.imePadding // 👈 Important for keyboard
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import eu.tutorials.gemverse.ui.theme.ColorModalMessage
import eu.tutorials.gemverse.ui.theme.ColorUserMessage
import kotlinx.coroutines.delay


@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = modifier
    ) {
        AppHeader()

        // Message list with weight to avoid pushing Input out
        MessageList(
            modifier = Modifier
                .weight(1f),
            viewModel.messageList
        )

        MessageInput(onMessageSend = {
            viewModel.sendMessage(it)
        })
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
//                .fillMaxWidth()
//                .fillMaxHeight(), // 👈 Not fillMaxSize
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_firstpage),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(200.dp)
            )
            Text(text = "Ask me anything", fontSize = 22.sp )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.toList().asReversed()) {
                MessageRow(messageModal = it)
            }
        }
    }
}

@Composable
fun MessageRow(messageModal: MessageModel) {
    val isModal = messageModal.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(if (isModal) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModal) 8.dp else 70.dp,
                        end = if (isModal) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModal) ColorModalMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {
                Text(
                    text = messageModal.message,
                    fontWeight = FontWeight.W500,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type your message...") }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
                keyboardController?.hide() // Optional: hide keyboard after send
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}

/*
@Composable
fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = "My Bot",
            color = Color.White,
            fontSize = 32.sp
        )
    }
}
*/
@Composable
fun AppHeader() {
    val emojis = listOf("🤖", "💬", "😄", "🧠", "📡", "👾", "🗨️")
    var currentEmojiIndex by remember { mutableStateOf(0) }

    // Change emoji every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000L)
            currentEmojiIndex = (currentEmojiIndex + 1) % emojis.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        contentAlignment = Alignment.Center // Center content in the box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "My Bot",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = emojis[currentEmojiIndex],
                fontSize = 28.sp
            )
        }
    }
}

