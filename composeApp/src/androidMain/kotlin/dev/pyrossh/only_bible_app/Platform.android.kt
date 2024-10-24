package dev.pyrossh.only_bible_app

import android.view.SoundEffectConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pyrossh.only_bible_app.domain.Verse
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import dev.pyrossh.only_bible_app.config.BuildKonfig

@Composable
actual fun getScreenWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp

@Composable
actual fun getScreenHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp

@Composable
actual fun playClickSound() = LocalView.current.playSoundEffect(SoundEffectConstants.CLICK)

actual fun shareVerses(verses: List<Verse>) {
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
