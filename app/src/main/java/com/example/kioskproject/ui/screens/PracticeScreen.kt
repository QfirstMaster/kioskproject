package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kioskproject.ui.components.BottomNavBar
import com.example.kioskproject.navigation.Screen

// 연습 카테고리 데이터 클래스
data class PracticeCategory(
    val emoji: String,
    val title: String,
    val desc: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(navController: NavController) {
    // 연습 카테고리 목록
    val categories = listOf(
        PracticeCategory("🎬", "영화관", "좌석 선택 · 티켓 발권", Screen.PracticeMovie.route),
        PracticeCategory("☕", "카페", "음료 · 사이즈 선택", Screen.PracticeCafe.route),
        PracticeCategory("🍔", "패스트푸드", "세트 메뉴 · 결제", Screen.PracticeFastFood.route),
        PracticeCategory("🍱", "음식점", "메뉴 선택 · 테이블", Screen.PracticeRestaurant.route)
    )

    Scaffold(
        topBar = {
            // 상단 뒤로가기 바
            TopAppBar(
                title = { Text("키오스크 연습하기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Practice.route) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "장소를 선택하세요",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 2열 그리드로 카테고리 카드 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
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

// 카테고리 카드 컴포넌트 (연습/퀴즈 공용)
@Composable
fun CategoryCard(
    emoji: String,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
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
}