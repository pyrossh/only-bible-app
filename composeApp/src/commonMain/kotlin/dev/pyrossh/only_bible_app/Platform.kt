package dev.pyrossh.only_bible_app

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import dev.pyrossh.only_bible_app.domain.Verse


@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
expect fun playClickSound()

@Composable
expect fun rememberShareVerses(): (verses: List<Verse>) -> Unit

@Composable
expect fun onThemeChange(themeType: ThemeType)

expect object SpeechService {
    fun init(onStarted: () -> Unit, onEnded: () -> Unit)
    fun dispose(onStarted: () -> Unit, onEnded: () -> Unit)
    fun startTextToSpeech(voiceName: String, text: String)
    fun stopTextToSpeech()
}