package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
fun SettingsScreen(
    navController: NavController,
    // ViewModel 주입 (설정값 저장/불러오기 담당)
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // ViewModel에서 상태값 구독
    val brightness by settingsViewModel.brightness.collectAsState()
    val sound      by settingsViewModel.sound.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val fontSize   by settingsViewModel.fontSize.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, Screen.Settings.route) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 화면 섹션
            SettingsSectionLabel("화면")

            // 밝기 슬라이더
            SettingsSliderRow(
                icon = Icons.Outlined.Star,
                label = "밝기 조절",
                value = brightness,
                onValueChange = { settingsViewModel.setBrightness(it) }
            )

            // 사운드 슬라이더
            SettingsSliderRow(
                icon = Icons.Outlined.Notifications,
                label = "사운드 조절",
                value = sound,
                onValueChange = { settingsViewModel.setSound(it) }
            )

            // 다크모드 토글
            SettingsToggleRow(
                icon = Icons.Outlined.Star,
                label = "다크모드",
                checked = isDarkMode,
                onCheckedChange = { settingsViewModel.setDarkMode(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 글씨 크기 섹션
            SettingsSectionLabel("글씨 크기")

            // 일반용 / 어르신용 버튼
            SettingsFontSizeRow(
                currentSize = fontSize,
                onSizeChange = { settingsViewModel.setFontSize(it) }
            )
        }
    }
}

// 섹션 레이블
@Composable
fun SettingsSectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

// 슬라이더 설정 행
@Composable
fun SettingsSliderRow(
    icon: ImageVector,
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
            Text(label, modifier = Modifier.width(80.dp), fontSize = 14.sp)
            Slider(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f)
            )
            // 퍼센트 표시
            Text(
                text = "${(value * 100).toInt()}%",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

// 토글 설정 행
@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
            Text(label, modifier = Modifier.weight(1f), fontSize = 14.sp)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

// 글씨 크기 선택 행 (일반용 / 어르신용)
@Composable
fun SettingsFontSizeRow(
    currentSize: String,
    onSizeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 일반용 버튼
            Button(
                onClick = { onSizeChange("normal") },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    // 선택된 버튼은 강조색, 아니면 흐리게
                    containerColor = if (currentSize == "normal")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("일반용", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("기본 크기", fontSize = 11.sp)
                }
            }

            // 어르신용 버튼
            Button(
                onClick = { onSizeChange("senior") },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentSize == "senior")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("어르신용", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("큰 크기", fontSize = 11.sp)
                }
            }
        }
    }
}