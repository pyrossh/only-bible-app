package dev.pyrossh.only_bible_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp


enum class Platform {
    Android,
    IOS
}

expect fun getPlatform(): Platform

@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
expect fun playClickSound()

expect object ShareKit {
    fun shareText(text: String)
}

@Composable
expect fun onThemeChange(themeType: ThemeType)

expect object SpeechService {
    fun init(onStarted: () -> Unit, onEnded: () -> Unit)
    fun dispose(onStarted: () -> Unit, onEnded: () -> Unit)
    fun startTextToSpeech(voiceName: String, text: String)
    fun stopTextToSpeech()
}