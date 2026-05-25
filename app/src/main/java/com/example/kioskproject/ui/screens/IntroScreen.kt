package com.example.kioskproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kioskproject.navigation.Screen

@Composable
fun IntroScreen(navController: NavController) {
    // 전체 화면을 어두운 배경으로 채움
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 앱 아이콘 이모지
            Text(text = "🖥️", fontSize = 64.sp)

            // 앱 제목
            Text(
                text = "키오스크 도우미",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // 부제목
            Text(
                text = "키오스크가 어렵지 않아요\n연습하고, 익히고, 자신 있게!",
                color = Color(0xFF8AB4D4),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 시작하기 버튼 → 메인 화면으로 이동
            Button(
                onClick = { navController.navigate(Screen.Main.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A9EFF)
                ),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(52.dp)
            ) {
                Text(
                    text = "시작하기 →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}