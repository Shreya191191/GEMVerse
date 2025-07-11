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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
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
import eu.tutorials.gemverse.ui.theme.DeepTeal
import eu.tutorials.gemverse.ui.theme.FrenchGray
import eu.tutorials.gemverse.ui.theme.MintGreen
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


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f) //Drawer width
                    .wrapContentHeight() //Only as tall as content
                    .padding(top = 50.dp) //Push below
                    .background(DeepTeal)
            ) {
                AppDrawer(drawerValue = drawerState.currentValue) { selectedRoute ->
                    scope.launch { drawerState.close() }
                    onDrawerItemClick(selectedRoute)
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


            MessageList(
                modifier = Modifier.weight(1f),
                messageList = viewModel.messageList
            )

            MessageInput(onMessageSend = {
                viewModel.sendMessage(it)
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
    onMenuClick:() -> Unit ={}
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
        onItemClick:(String) -> Unit
    ){
        val currentUserEmail= FirebaseAuth.getInstance().currentUser?.email ?: "No Email"
        var isGamesExpanded by remember {mutableStateOf(false)}
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
                .background(MintGreen)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Email
            val clipboardManager = LocalClipboardManager.current
            val context = LocalContext.current
            DrawerRow(
                R.drawable.ic_profileicon, currentUserEmail,
                selected = selectedItem == currentUserEmail
            ){
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
            ){
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
            ){
                selectedItem = "Help"; onItemClick("help")
            }
            DrawerRow(
                R.drawable.ic_logout, "Log out",
                selected = selectedItem == "Log out"
            ){
                selectedItem = "Log out";
                onItemClick(Screen.LogOut.route)
            }

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
                ){
                    selectedItem = "Tic Tac Toe";
                    onItemClick(Screen.TicTacToe.route)
                }
                GameDrawerItem(
                    "Quiz Game",
                    selected = selectedItem == "Quiz Game"
                ){
                    selectedItem = "Quiz Game";
                    onItemClick(Screen.QuizFlow.route)
                }
                GameDrawerItem(
                    "Captain Game",
                    selected = selectedItem == "Captain Game"
                ){
                    selectedItem = "Captain Game";
                    onItemClick(Screen.CaptainGame.route)
                }
                GameDrawerItem(
                    "Snake Game",
                    selected = selectedItem == "Snake Game"
                ){
                    selectedItem = "Snake Game";
                    onItemClick("snake_game")
                }

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

