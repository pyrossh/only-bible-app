package dev.pyrossh.only_bible_app

import android.content.Intent
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pyrossh.only_bible_app.domain.Verse
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import dev.pyrossh.only_bible_app.config.BuildKonfig
import theme.darkScheme
import theme.lightScheme

@Composable
actual fun getScreenWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun getScreenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp

@Composable
actual fun playClickSound() = LocalView.current.playSoundEffect(SoundEffectConstants.CLICK)

@Composable
actual fun  rememberShareVerses(): (verses: List<Verse>) -> Unit {
    val context = LocalContext.current
    return { verses ->
        val versesThrough =
            if (verses.size >= 3) "${verses.first().verseIndex + 1}-${verses.last().verseIndex + 1}" else verses.map { it.verseIndex + 1 }
                .joinToString(",")
        val title = "${verses[0].bookName} ${verses[0].chapterIndex + 1}:${versesThrough}"
        val text = verses.joinToString("\n") {
            it.text.replace("<span style=\"color:red;\">", "")
                .replace("<em>", "")
                .replace("</span>", "")
                .replace("</em>", "")
        }
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${title}\n${text}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}

@Composable
actual fun onThemeChange(themeType: ThemeType) {
    val isLight = isLightTheme(themeType, isSystemInDarkTheme())
    val colorScheme = if (isLight) lightScheme else darkScheme
    val context = LocalContext.current as ComponentActivity
    LaunchedEffect(key1 = themeType) {
        context.enableEdgeToEdge(
            statusBarStyle = if (isLight) {
                SystemBarStyle.light(
                    colorScheme.background.toArgb(),
                    colorScheme.onBackground.toArgb()
                )
            } else {
                SystemBarStyle.dark(
                    colorScheme.background.toArgb(),
                )
            },
            navigationBarStyle = if (isLight) {
                SystemBarStyle.light(
                    colorScheme.background.toArgb(),
                    colorScheme.onBackground.toArgb()
                )
            } else {
                SystemBarStyle.dark(
                    colorScheme.background.toArgb(),
                )
            }
        )
    }
}

actual object SpeechService {
    val speechSynthesizer = SpeechSynthesizer(
        SpeechConfig.fromSubscription(
            BuildKonfig.SUBSCRIPTION_KEY,
            BuildKonfig.SUBSCRIPTION_REGION,
        )
    )

    actual fun init(onStarted: () -> Unit, onEnded: () -> Unit) {
        speechSynthesizer.SynthesisStarted.addEventListener { _: Any, _: SpeechSynthesisEventArgs ->
            onStarted()
        }
        speechSynthesizer.SynthesisCompleted.addEventListener { _: Any, _: SpeechSynthesisEventArgs ->
            onEnded()
        }
    }

    actual fun dispose(onStarted: () -> Unit, onEnded: () -> Unit) {
//            speechService.SynthesisStarted.removeEventListener(started)
//            speechService.SynthesisCompleted.removeEventListener(completed)
    }

    actual fun startTextToSpeech(voiceName: String, text: String) {
        speechSynthesizer.StartSpeakingSsml(
            """
            <speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis" xml:lang="en-US">
                <voice name="$voiceName">
                    $text
                </voice>
            </speak>
        """
        )
    }

    actual fun stopTextToSpeech() {
        speechSynthesizer.StopSpeakingAsync()
    }
}
