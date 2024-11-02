package dev.pyrossh.only_bible_app

import android.app.Activity
import android.app.Activity.RESULT_OK
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

actual fun getPlatform() = Platform.Android

@Composable
actual fun getScreenWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun getScreenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp

@Composable
actual fun playClickSound() = LocalView.current.playSoundEffect(SoundEffectConstants.CLICK)


actual object ShareKit {

    private var activityProvider: () -> Activity = {
        throw IllegalArgumentException(
            "You need to implement the 'activityProvider' to provide the required Activity. " +
                    "Just make sure to set a valid activity using " +
                    "the 'setActivityProvider()' method."
        )
    }

    fun setActivityProvider(provider: () -> Activity) {
        activityProvider = provider
    }

    actual fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val intentChooser = Intent.createChooser(intent, null)
        activityProvider.invoke().startActivity(intentChooser)
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
