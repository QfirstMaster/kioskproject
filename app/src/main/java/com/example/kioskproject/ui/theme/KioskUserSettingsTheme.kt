package com.example.kioskproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun KioskUserSettingsTheme(
    isDarkMode: Boolean,
    isSeniorMode: Boolean,
    content: @Composable () -> Unit
) {
    // 어르신 모드일 때 글씨 크기 확대
    val typography = if (isSeniorMode) {
        MaterialTheme.typography.copy(
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontSize = 30.sp),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontSize = 34.sp),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp)
        )
    } else {
        MaterialTheme.typography
    }

    // 기존 프로젝트 테마 재사용
    KioskprojectTheme(
        darkTheme = isDarkMode
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = typography,
            content = content
        )
    }
}