package eu.tutorials.gemverse.quizgame.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.gemverse.ui.theme.NavyBlue

@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onGoToChat: () -> Unit
    ){
    var animated by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animated) 1f else 0.5f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ), label = "popupAnimation"
    )

    LaunchedEffect(Unit) {
        animated = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBlue), // Navy blue background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(scale) // 🎉 Animate this!
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "QUIZ COMPLETED!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Score: $score / $total",
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(30.dp))
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .height(50.dp)
                    .width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF001F54)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onGoToChat,
                modifier = Modifier
                    .height(50.dp)
                    .width(300.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF001F54)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Go To My Bot", fontSize = 24.sp)
            }

        }
    }
}