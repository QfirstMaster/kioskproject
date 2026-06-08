package com.example.kioskproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kioskproject.ui.screens.IntroScreen
import com.example.kioskproject.ui.screens.MainScreen
import com.example.kioskproject.ui.screens.PracticeDetailScreen
import com.example.kioskproject.ui.screens.PracticeScreen
import com.example.kioskproject.ui.screens.QuizDetailScreen
import com.example.kioskproject.ui.screens.QuizScreen
import com.example.kioskproject.ui.screens.SettingsScreen
import com.example.kioskproject.ui.theme.KioskUserSettingsTheme
import com.example.kioskproject.viewmodel.SettingsViewModel

@Composable
fun AppNavGraph(
    settingsViewModel: SettingsViewModel = viewModel()
) {

    // 저장된 다크모드 상태 가져오기
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    // 저장된 글씨 크기 설정 가져오기
    // "normal" 또는 "senior"
    val fontSize by settingsViewModel.fontSize.collectAsState()

    // 어르신 모드 여부 확인
    val isSeniorMode = fontSize == "senior"

    // 사용자 설정 테마 적용
    KioskUserSettingsTheme(
        isDarkMode = isDarkMode,
        isSeniorMode = isSeniorMode
    ) {

        // 네비게이션 컨트롤러 생성 (화면 이동 담당)
        val navController = rememberNavController()

        // NavHost: 시작 화면을 Intro로 설정
        NavHost(
            navController = navController,
            startDestination = Screen.Intro.route
        ) {

            // 메인 화면 연결
            composable(Screen.Intro.route) {
                IntroScreen(navController)
            }

            composable(Screen.Main.route) {
                MainScreen(navController)
            }

            composable(Screen.Practice.route) {
                PracticeScreen(navController)
            }

            composable(Screen.Quiz.route) {
                QuizScreen(navController)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(navController)
            }

            // 연습 세부 화면 연결
            composable(Screen.PracticeMovie.route) {
                PracticeDetailScreen(
                    navController,
                    "movie"
                )
            }

            composable(Screen.PracticeCafe.route) {
                PracticeDetailScreen(
                    navController,
                    "cafe"
                )
            }

            composable(Screen.PracticeFastFood.route) {
                PracticeDetailScreen(
                    navController,
                    "fastfood"
                )
            }

            composable(Screen.PracticeRestaurant.route) {
                PracticeDetailScreen(
                    navController,
                    "restaurant"
                )
            }

            // 퀴즈 세부 화면 연결
            composable(Screen.QuizMovie.route) {
                QuizDetailScreen(
                    navController,
                    "movie"
                )
            }

            composable(Screen.QuizCafe.route) {
                QuizDetailScreen(
                    navController,
                    "cafe"
                )
            }

            composable(Screen.QuizFastFood.route) {
                QuizDetailScreen(
                    navController,
                    "fastfood"
                )
            }

            composable(Screen.QuizRestaurant.route) {
                QuizDetailScreen(
                    navController,
                    "restaurant"
                )
            }
        }
    }
}