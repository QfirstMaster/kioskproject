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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

// 연습/퀴즈 카테고리 데이터 클래스
data class PracticeCategory(
    val emoji: String,
    val title: String,
    val desc: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // 설정에서 저장한 글씨 모드 가져오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    // "senior"이면 어르신 모드 적용
    val isSeniorMode = fontSizeMode == "senior"

    // 연습 카테고리 목록
    val categories = listOf(
        PracticeCategory("🎬", "영화관", "팝콘 · 음료 선택", Screen.PracticeMovie.route),
        PracticeCategory("☕", "카페", "음료 · 사이즈 선택", Screen.PracticeCafe.route),
        PracticeCategory("🍔", "패스트푸드", "세트 메뉴 · 결제", Screen.PracticeFastFood.route),
        PracticeCategory("🍱", "음식점", "메뉴 선택 · 테이블", Screen.PracticeRestaurant.route)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "키오스크 연습하기",
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
            BottomNavBar(navController, Screen.Practice.route)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(if (isSeniorMode) 26.dp else 20.dp)
        ) {
            Text(
                text = "장소를 선택하세요",
                fontSize = if (isSeniorMode) 18.sp else 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(
                    bottom = if (isSeniorMode) 22.dp else 16.dp
                )
            )

            // 항상 1열로 카드 배치
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 22.dp else 14.dp
                )
            ) {
                items(categories) { category ->
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

// 카테고리 카드 컴포넌트
// PracticeScreen, QuizScreen에서 같이 사용
@Composable
fun CategoryCard(
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isSeniorMode) 24.dp else 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                if (isSeniorMode) 22.dp else 14.dp
            )
        ) {
            Text(
                text = emoji,
                fontSize = if (isSeniorMode) 48.sp else 32.sp
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 10.dp else 6.dp
                )
            ) {
                Text(
                    text = title,
                    fontSize = if (isSeniorMode) 28.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = desc,
                    fontSize = if (isSeniorMode) 20.sp else 13.sp,
                    color = Color.Gray,
                    lineHeight = if (isSeniorMode) 28.sp else 20.sp
                )
            }
        }
    }
}