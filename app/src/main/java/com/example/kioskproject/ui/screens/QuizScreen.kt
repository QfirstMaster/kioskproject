package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kioskproject.ui.components.BottomNavBar
import com.example.kioskproject.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    // 퀴즈 카테고리 목록
    val categories = listOf(
        PracticeCategory("🎬", "영화관 문제", "5문항", Screen.QuizMovie.route),
        PracticeCategory("☕", "카페 문제", "5문항", Screen.QuizCafe.route),
        PracticeCategory("🍔", "패스트푸드 문제", "5문항", Screen.QuizFastFood.route),
        PracticeCategory("🍱", "음식점 문제", "5문항", Screen.QuizRestaurant.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("키오스크 문제 풀기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Quiz.route) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "카테고리를 선택하세요",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 2열 그리드로 퀴즈 카테고리 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    // PracticeScreen에서 만든 CategoryCard 재사용
                    CategoryCard(
                        emoji = category.emoji,
                        title = category.title,
                        desc = category.desc,
                        onClick = { navController.navigate(category.route) }
                    )
                }
            }
        }
    }
}