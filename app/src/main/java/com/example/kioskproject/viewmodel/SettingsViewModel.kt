package com.example.kioskproject.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// DataStore 초기화
private val android.content.Context.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    // 설정 키 정의
    companion object {
        val BRIGHTNESS_KEY  = floatPreferencesKey("brightness")
        val SOUND_KEY       = floatPreferencesKey("sound")
        val DARK_MODE_KEY   = booleanPreferencesKey("dark_mode")
        val FONT_SIZE_KEY   = stringPreferencesKey("font_size") // "normal" or "senior"
    }

    // 상태값 (UI에서 구독)
    private val _brightness  = MutableStateFlow(0.75f)
    private val _sound       = MutableStateFlow(0.6f)
    private val _isDarkMode  = MutableStateFlow(false)
    private val _fontSize    = MutableStateFlow("normal") // "normal" or "senior"

    val brightness  = _brightness.asStateFlow()
    val sound       = _sound.asStateFlow()
    val isDarkMode  = _isDarkMode.asStateFlow()
    val fontSize    = _fontSize.asStateFlow()

    init {
        // 앱 시작 시 저장된 설정 불러오기
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _brightness.value = prefs[BRIGHTNESS_KEY] ?: 0.75f
                _sound.value      = prefs[SOUND_KEY]      ?: 0.6f
                _isDarkMode.value = prefs[DARK_MODE_KEY]  ?: false
                _fontSize.value   = prefs[FONT_SIZE_KEY]  ?: "normal"
            }
        }
    }

    // 밝기 저장
    fun setBrightness(value: Float) {
        viewModelScope.launch {
            _brightness.value = value
            context.dataStore.edit { it[BRIGHTNESS_KEY] = value }
        }
    }

    // 사운드 저장
    fun setSound(value: Float) {
        viewModelScope.launch {
            _sound.value = value
            context.dataStore.edit { it[SOUND_KEY] = value }
        }
    }

    // 다크모드 저장
    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            _isDarkMode.value = value
            context.dataStore.edit { it[DARK_MODE_KEY] = value }
        }
    }

    // 글씨 크기 저장 ("normal" or "senior")
    fun setFontSize(value: String) {
        viewModelScope.launch {
            _fontSize.value = value
            context.dataStore.edit { it[FONT_SIZE_KEY] = value }
        }
    }
}