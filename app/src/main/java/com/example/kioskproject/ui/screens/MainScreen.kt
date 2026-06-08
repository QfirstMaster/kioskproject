package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.navigation.Screen
import com.example.kioskproject.ui.components.BottomNavBar
import com.example.kioskproject.viewmodel.SettingsViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MainScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // 설정에 저장된 글씨 크기 모드 가져오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    // "senior"이면 어르신모드 적용
    val isSeniorMode = fontSizeMode == "senior"

    Scaffold(
        // 하단 네비게이션 바
        bottomBar = {
            BottomNavBar(navController, Screen.Main.route)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // 화면 내용이 길어지면 세로 스크롤 가능
                .padding(if (isSeniorMode) 26.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(
                if (isSeniorMode) 22.dp else 16.dp
            )
        ) {
            // 상단 타이틀
            Text(
                text = "🖥️ 키오스크 도우미",
                fontSize = if (isSeniorMode) 30.sp else 22.sp,
                fontWeight = FontWeight.Bold
            )

            // 메뉴 안내 텍스트
            Text(
                text = "메뉴",
                fontSize = if (isSeniorMode) 18.sp else 12.sp,
                color = Color.Gray
            )

            // 메인 메뉴 카드 3개
            // 기존 Row 3열 구조 대신 Column 1열 구조로 변경
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 22.dp else 14.dp
                )
            ) {
                // 연습하기 카드
                MainMenuCard(
                    emoji = "🎮",
                    title = "키오스크 연습하기",
                    desc = "실제처럼 주문을 연습해봐요",
                    isSeniorMode = isSeniorMode,
                    onClick = {
                        navController.navigate(Screen.Practice.route)
                    }
                )

                // 문제풀기 카드
                MainMenuCard(
                    emoji = "📝",
                    title = "문제 풀기",
                    desc = "퀴즈로 실력을 확인해요",
                    isSeniorMode = isSeniorMode,
                    onClick = {
                        navController.navigate(Screen.Quiz.route)
                    }
                )

                // 설정 카드
                MainMenuCard(
                    emoji = "⚙️",
                    title = "설정",
                    desc = "화면·소리·글씨를 조절해요",
                    isSeniorMode = isSeniorMode,
                    onClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
        }
    }
}

// 메인 메뉴 카드 컴포넌트
@Composable
fun MainMenuCard(
    emoji: String,
    title: String,
    desc: String,
    isSeniorMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isSeniorMode) 210.dp else 140.dp),
        shape = RoundedCornerShape(
            if (isSeniorMode) 24.dp else 16.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSeniorMode) 24.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(
                if (isSeniorMode) 10.dp else 6.dp
            )
        ) {
            // 카드 이모지
            Text(
                text = emoji,
                fontSize = if (isSeniorMode) 48.sp else 32.sp
            )

            // 카드 제목
            Text(
                text = title,
                fontSize = if (isSeniorMode) 28.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )

            // 카드 설명
            Text(
                text = desc,
                fontSize = if (isSeniorMode) 20.sp else 13.sp,
                color = Color.Gray,
                lineHeight = if (isSeniorMode) 28.sp else 20.sp
            )
        }
    }
}