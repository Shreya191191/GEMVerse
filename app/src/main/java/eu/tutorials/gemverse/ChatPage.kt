package eu.tutorials.gemverse

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.gemverse.ui.theme.ColorModalMessage
import eu.tutorials.gemverse.ui.theme.ColorUserMessage
import eu.tutorials.gemverse.ui.theme.FrenchGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
    onDrawerItemClick: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // ✅ NEW: Observe sessions
    val sessions = viewModel.sessionList


    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Open) {
            println("Drawer khula, sessions load kar rahe hain...")
            viewModel.loadSessions()
        }
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(0.75f) //Drawer width
//                  //  .wrapContentHeight() //Only as tall as content
//                    .fillMaxHeight()
//                    .padding(top = 50.dp) //Push below AppBar
//                  //  .background(DeepTeal)
//            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight()
                   // .background(DeepTeal)
                    .background(Color.White)
            )  {
//                AppDrawer(drawerValue = drawerState.currentValue) { selectedRoute ->
//                    scope.launch { drawerState.close() }
//                    onDrawerItemClick(selectedRoute)
//                }
//                AppDrawer(
//                    drawerValue = drawerState.currentValue,
//                    sessions = sessions
//                ) { selectedRoute ->
//                    scope.launch { drawerState.close() }
//                    if (sessions.any { it.sessionId == selectedRoute }) {
//                        viewModel.loadMessagesFromSession(selectedRoute)
//                    } else {
//                        onDrawerItemClick(selectedRoute)
//                    }
//                }
                AppDrawer(
                    drawerValue = drawerState.currentValue,
                    sessions = sessions
                ) { selectedRoute ->
                    scope.launch { drawerState.close() }
                    if (sessions.any { it.sessionId == selectedRoute }) {
                        viewModel.loadMessagesFromSession(selectedRoute)
                    } else if (selectedRoute == "new_chat") {
                        viewModel.currentSessionId = null
                        viewModel.messageList.clear()
                    } else {
                        onDrawerItemClick(selectedRoute)
                    }
                }


            }
        }

    )
    {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AppHeader(onMenuClick = {
                scope.launch { drawerState.open() }
            })
            // ✅ NEW:
//            if (drawerState.currentValue == DrawerValue.Open && sessions.isNotEmpty()) {
//                // Drawer khula hai → sessions dikhao
//                ChatSessionList(
//                    sessions = sessions,
//                    onSessionClick = {
//                        viewModel.loadMessagesFromSession(it.sessionId)
//                    }
//                )
//            } else {
//                // Drawer band hai → normal chat dikhao
//                MessageList(
//                    modifier = Modifier.weight(1f),
//                    messageList = viewModel.messageList
//                )
//            }



            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList
            )

//            MessageInput(onMessageSend = {
//                viewModel.sendMessage(it)
//            })

            MessageInput(onMessageSend = { msg ->
                if (viewModel.currentSessionId == null) {
                    viewModel.createSessionAndSendMessage(msg)
                } else {
                    viewModel.sendMessage(msg)
                }
            })

        }
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id= R.drawable.ic_firstpage),
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
                SelectionContainer {
                    Text(
                        text = messageModal.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White,
                        fontSize = 16.sp,
                        softWrap = true
                    )
                }
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

@Composable
fun AppHeader(
    onMenuClick:() -> Unit ={},
  //  title: String = "New Chat"
) {
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
//            .padding(16.dp),
//        contentAlignment = Alignment.Center // Center content in the box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))


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

    @Composable
    fun AppDrawer(
        drawerValue: DrawerValue,
        sessions: List<ChatSessionModel>,
        onItemClick:(String) -> Unit
    ) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"
        var isGamesExpanded by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf("") } // ✅ NEW: Track selected item

        LaunchedEffect(drawerValue) {
            if (drawerValue == DrawerValue.Open) {
                selectedItem = ""
                isGamesExpanded = false
            }
        }



        Column(
            modifier = Modifier
                // .wrapContentHeight()
                .fillMaxWidth()
                // .background(MintGreen)
                .background((Color.White))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // Email
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            DrawerRow(
                R.drawable.ic_profileicon, currentUserEmail,
                selected = selectedItem == currentUserEmail
            ) {
                clipboardManager.setText(AnnotatedString(currentUserEmail))
                Toast.makeText(context, "User ID copied!", Toast.LENGTH_SHORT).show()
                selectedItem = currentUserEmail;
                onItemClick(currentUserEmail)
            }

            // Games (Expandable Header with Arrow Icon)
            val backgroundColor = if (selectedItem == "Games") FrenchGray else Color.Transparent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isGamesExpanded = !isGamesExpanded
                        selectedItem = if (isGamesExpanded) "Games" else ""
                    }
                    .background(backgroundColor)
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_game),
                    contentDescription = "Games",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp) // ✅ Bigger icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Games", color = Color.Black, fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(
                        id = if (isGamesExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
                    ),
                    contentDescription = "Expand",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Other Drawer Items with highlight on selection
            DrawerRow(
                R.drawable.ic_settings, "Settings",
                selected = selectedItem == "Settings"
            ) {
                selectedItem = "Settings"; onItemClick("settings")
            }

            Divider( // 👈 This line adds the black divider
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            DrawerRow(
                R.drawable.ic_help, "Help",
                selected = selectedItem == "Help"
            ) {
                selectedItem = "Help"; onItemClick("help")
            }
            DrawerRow(
                R.drawable.ic_logout, "Log out",
                selected = selectedItem == "Log out"
            ) {
                selectedItem = "Log out"; onItemClick("logout")
            }

            Spacer(modifier = Modifier.height(12.dp))
            // ➡ NEW CHAT ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick("new_chat")
                    }
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Chat",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Divider(color = Color.Black, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            if (isGamesExpanded) {
                Spacer(modifier = Modifier.height(8.dp)) // Optional spacing
                Divider(
                    color = Color.Black,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                GameDrawerItem(
                    "Tic Tac Toe",
                    selected = selectedItem == "Tic Tac Toe"
                ) {
                    selectedItem = "Tic Tac Toe";
                    onItemClick(Screen.TicTacToe.route)
                }
                GameDrawerItem(
                    "Quiz Game",
                    selected = selectedItem == "Quiz Game"
                ) {
                    selectedItem = "Quiz Game";
                    onItemClick(Screen.QuizFlow.route)
                }
                GameDrawerItem(
                    "Captain Game",
                    selected = selectedItem == "Captain Game"
                ) {
                    selectedItem = "Captain Game";
                    onItemClick("captain_game")
                }
                GameDrawerItem(
                    "Snake Game",
                    selected = selectedItem == "Snake Game"
                ) {
                    selectedItem = "Snake Game";
                    onItemClick("snake_game")
                }

            }
//            if (sessions.isNotEmpty()) {
//                Text(
//                    text = "Chat Sessions",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//                sessions.forEach { session ->
//                    Text(
//                        text = session.sessionId,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onItemClick(session.sessionId)
//                            }
//                            .padding(8.dp),
//                        color = Color.Black
//                    )
//                }
//            }
//
//        }

            if (sessions.isNotEmpty()) {
                Text(
                    text = "Chat Sessions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                sessions.forEach { session ->
                    Text(
                        text = session.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(session.sessionId)
                            }
                            .padding(8.dp),
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = "No sessions found.",
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }


        }
    }

    @Composable
    fun DrawerRow(icon: Int, text: String, selected: Boolean = false, onClick: () -> Unit = {}) {
        val backgroundColor = if (selected) FrenchGray else Color.Transparent // ✅ Highlight if selected
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(backgroundColor)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontSize = 20.sp, color = Color.Black)
        }
    }

    @Composable
    fun GameDrawerItem(
        title: String,
        selected: Boolean = false,
        onClick: () -> Unit
    ){
        val backgroundColor = if (selected) Color(0xFF005050) else Color.Transparent

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(backgroundColor)
                .padding(start = 32.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = title,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = Color.Black, fontSize = 16.sp)
        }
    }

