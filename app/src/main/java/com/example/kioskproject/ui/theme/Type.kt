package com.example.kioskproject.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kioskproject.viewmodel.SettingsViewModel

/*
    어르신 모드 여부에 따라 글씨 크기 변경
*/
@Composable
fun getFontSize(
    normal: Int,
    senior: Int
): TextUnit {

    val settingsViewModel: SettingsViewModel = viewModel()

    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    return if (fontSizeMode == "senior") {
        senior.sp
    } else {
        normal.sp
    }
}

/*
    어르신 모드 여부에 따라 버튼 높이 변경
*/
@Composable
fun getButtonHeight(
    normal: Int,
    senior: Int
): TextUnit {

    val settingsViewModel: SettingsViewModel = viewModel()

    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    return if (fontSizeMode == "senior") {
        senior.sp
    } else {
        normal.sp
    }
}