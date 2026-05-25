package com.example.kioskproject.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.navigation.NavController
import com.example.kioskproject.navigation.Screen

// 하단 네비게이션 아이템 데이터 클래스
data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    // 네비게이션 아이템 목록
    val items = listOf(
        NavItem("홈", Icons.Outlined.Home, Screen.Main.route),
        NavItem("연습", Icons.Outlined.PlayArrow, Screen.Practice.route),
        NavItem("퀴즈", Icons.Outlined.Edit, Screen.Quiz.route),
        NavItem("설정", Icons.Outlined.Settings, Screen.Settings.route)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                // 현재 화면과 일치하면 선택 상태로 표시
                selected = currentRoute == item.route,
                onClick = {
                    // 같은 화면이면 이동 안 함
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // 백스택 쌓이지 않게 설정
                            popUpTo(Screen.Main.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}