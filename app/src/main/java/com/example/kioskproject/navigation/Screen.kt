package com.example.kioskproject.navigation

sealed class Screen(val route: String) {
    object Intro : Screen("intro")
    object Main : Screen("main")
    object Practice : Screen("practice")
    object Quiz : Screen("quiz")
    object Settings : Screen("settings")

    // 연습 세부 화면
    object PracticeMovie : Screen("practice/movie")
    object PracticeCafe : Screen("practice/cafe")
    object PracticeFastFood : Screen("practice/fastfood")
    object PracticeRestaurant : Screen("practice/restaurant")

    // 퀴즈 세부 화면
    object QuizMovie : Screen("quiz/movie")
    object QuizCafe : Screen("quiz/cafe")
    object QuizFastFood : Screen("quiz/fastfood")
    object QuizRestaurant : Screen("quiz/restaurant")
}