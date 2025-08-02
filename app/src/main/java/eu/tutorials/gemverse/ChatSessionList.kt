package eu.tutorials.gemverse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun ChatSessionList(
    sessions: List<ChatSessionModel>,
    onSessionClick: (ChatSessionModel) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(sessions) { session ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSessionClick(session) }
                    .padding(12.dp)
                    .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = session.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = session.timestamp.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

