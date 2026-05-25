package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kioskproject.ui.components.BottomNavBar
import com.example.kioskproject.navigation.Screen

@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        // 하단 네비게이션 바
        bottomBar = { BottomNavBar(navController, Screen.Main.route) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 상단 타이틀
            Text(
                text = "🖥️ 키오스크 도우미",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "메뉴",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // 메인 메뉴 카드 3개
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 연습하기 카드
                MainMenuCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🎮",
                    title = "키오스크\n연습하기",
                    desc = "실제처럼 주문을\n연습해봐요",
                    onClick = { navController.navigate(Screen.Practice.route) }
                )

                // 문제풀기 카드
                MainMenuCard(
                    modifier = Modifier.weight(1f),
                    emoji = "📝",
                    title = "문제\n풀기",
                    desc = "퀴즈로 실력을\n확인해요",
                    onClick = { navController.navigate(Screen.Quiz.route) }
                )

                // 설정 카드
                MainMenuCard(
                    modifier = Modifier.weight(1f),
                    emoji = "⚙️",
                    title = "설정",
                    desc = "화면·소리·글씨\n조절해요",
                    onClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        }
    }
}

// 메인 메뉴 카드 컴포넌트
@Composable
fun MainMenuCard(
    modifier: Modifier = Modifier,
    emoji: String,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}