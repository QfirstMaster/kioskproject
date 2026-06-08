package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.FormatSize
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.navigation.Screen
import com.example.kioskproject.ui.components.BottomNavBar
import com.example.kioskproject.util.applyAppBrightness
import com.example.kioskproject.util.playTestSound
import com.example.kioskproject.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // 저장된 설정값 가져오기
    val brightness by settingsViewModel.brightness.collectAsState()
    val sound by settingsViewModel.sound.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val fontSize by settingsViewModel.fontSize.collectAsState()

    val context = LocalContext.current

    // 밝기 값 변경 시 실제 앱 밝기 적용
    LaunchedEffect(brightness) {
        applyAppBrightness(context, brightness)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "설정")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },

        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = Screen.Settings.route
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(text = "앱 사용 설정")

            // 밝기 조절
            SettingSliderRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.LightMode,
                        contentDescription = "밝기"
                    )
                },
                label = "밝기 조절",
                description = "화면 밝기를 조절합니다.",
                value = brightness,
                onValueChange = {
                    settingsViewModel.setBrightness(it)
                }
            )

            // 소리 조절
            SettingSliderRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.VolumeUp,
                        contentDescription = "소리"
                    )
                },
                label = "소리 조절",
                description = "앱 효과음 크기를 조절합니다.",
                value = sound,
                onValueChange = {
                    settingsViewModel.setSound(it)
                },
                onValueChangeFinished = {
                    playTestSound(sound)
                }
            )

            // 다크모드
            SettingSwitchRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = "다크모드"
                    )
                },
                label = "다크모드",
                description = "어두운 화면으로 앱을 사용합니다.",
                checked = isDarkMode,
                onCheckedChange = {
                    settingsViewModel.setDarkMode(it)
                }
            )

            // 큰 글씨모드
            SettingSwitchRow(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.FormatSize,
                        contentDescription = "큰 글씨모드"
                    )
                },
                label = "큰 글씨모드",
                description = "글씨를 크게 표시합니다.",
                checked = fontSize == "senior",
                onCheckedChange = {
                    settingsViewModel.setFontSize(
                        if (it) "senior" else "normal"
                    )
                }
            )
        }
    }
}

/*
    슬라이더 설정 카드
    밝기 / 소리 조절에 사용
*/
@Composable
fun SettingSliderRow(
    icon: @Composable () -> Unit,
    label: String,
    description: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                icon()

                Spacer(modifier = Modifier.padding(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(text = label)
                        Text(text = description)
                    }

                    // 현재 퍼센트 표시
                    Text(
                        text = "${(value * 100).toInt()}%"
                    )
                }
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = 0f..1f
            )
        }
    }
}

/*
    스위치 설정 카드
    다크모드 / 큰 글씨모드에 사용
*/
@Composable
fun SettingSwitchRow(
    icon: @Composable () -> Unit,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            icon()

            Spacer(modifier = Modifier.padding(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = label)
                Text(text = description)
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}