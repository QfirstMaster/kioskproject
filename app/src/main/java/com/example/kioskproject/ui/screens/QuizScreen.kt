package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // 설정에서 저장한 글씨 모드 가져오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    // "senior"이면 어르신 모드 적용
    val isSeniorMode = fontSizeMode == "senior"

    // 퀴즈 카테고리 목록
    val categories = listOf(
        PracticeCategory("🎬", "영화관 문제", "콤보와 팝콘 사이드 퀴즈", Screen.QuizMovie.route),
        PracticeCategory("☕", "카페 문제", "음료 사이즈와 얼음선택 퀴즈", Screen.QuizCafe.route),
        PracticeCategory("🍔", "패스트푸드 문제", "햄버거 단품 및 세트 구성 퀴즈", Screen.QuizFastFood.route),
        PracticeCategory("🍱", "음식점 문제", "음식 주문 퀴즈", Screen.QuizRestaurant.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "키오스크 문제 풀기",
                        fontSize = if (isSeniorMode) 26.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController, Screen.Quiz.route)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(if (isSeniorMode) 26.dp else 20.dp)
        ) {
            Text(
                text = "카테고리를 선택하세요",
                fontSize = if (isSeniorMode) 18.sp else 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    bottom = if (isSeniorMode) 22.dp else 16.dp
                )
            )

            // 항상 1열로 퀴즈 카테고리 배치
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 22.dp else 14.dp
                )
            ) {
                items(categories) { category ->
                    // PracticeScreen에 있는 CategoryCard 재사용
                    CategoryCard(
                        emoji = category.emoji,
                        title = category.title,
                        desc = category.desc,
                        isSeniorMode = isSeniorMode,
                        onClick = {
                            navController.navigate(category.route)
                        }
                    )
                }
            }
        }
    }
}