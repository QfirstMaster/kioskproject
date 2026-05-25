package com.example.kioskproject.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavGraph() {
    // 네비게이션 컨트롤러 생성 (화면 이동을 담당)
    val navController = rememberNavController()

    // NavHost: 시작 화면을 Intro로 설정
    NavHost(
        navController = navController,
        startDestination = Screen.Intro.route
    ) {
        // 각 route에 해당하는 화면 연결
        composable(Screen.Intro.route)     { IntroScreen(navController) }
        composable(Screen.Main.route)      { MainScreen(navController) }
        composable(Screen.Practice.route)  { PracticeScreen(navController) }
        composable(Screen.Quiz.route)      { QuizScreen(navController) }
        composable(Screen.Settings.route)  { SettingsScreen(navController) }

        // 연습 세부 화면
        composable(Screen.PracticeMovie.route)      { PracticeDetailScreen(navController, "movie") }
        composable(Screen.PracticeCafe.route)       { PracticeDetailScreen(navController, "cafe") }
        composable(Screen.PracticeFastFood.route)   {
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

        // 퀴즈 세부 화면
        composable(Screen.QuizMovie.route)      { QuizDetailScreen(navController, "movie") }
        composable(Screen.QuizCafe.route)       { QuizDetailScreen(navController, "cafe") }
        composable(Screen.QuizFastFood.route)   { QuizDetailScreen(navController, "fastfood") }
        composable(Screen.QuizRestaurant.route) { QuizDetailScreen(navController, "restaurant") }
    }
}