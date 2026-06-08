package com.example.kioskproject.util

import android.media.AudioManager
import android.media.ToneGenerator

// 설정한 소리 크기로 테스트 효과음 재생
fun playTestSound(volume: Float) {
    val soundVolume = (volume.coerceIn(0f, 1f) * 100).toInt()

    val toneGenerator = ToneGenerator(
        AudioManager.STREAM_MUSIC,
        soundVolume
    )

    toneGenerator.startTone(
        ToneGenerator.TONE_PROP_BEEP,
        150
    )
}