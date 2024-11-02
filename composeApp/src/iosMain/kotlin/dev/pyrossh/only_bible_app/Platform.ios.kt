package dev.pyrossh.only_bible_app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pyrossh.only_bible_app.config.BuildKonfig
import dev.pyrossh.only_bible_app.domain.Verse
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import theme.darkScheme
import theme.lightScheme

//import platform.AVKit.Audio

actual fun getPlatform() = Platform.IOS

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidth(): Dp = LocalWindowInfo.current.containerSize.width.pxToPoint().dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenHeight(): Dp = LocalWindowInfo.current.containerSize.height.pxToPoint().dp

fun Int.pxToPoint(): Double = this.toDouble() / UIScreen.mainScreen.scale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun playClickSound() {
//    AudioServicesPlayAlertSound(SystemSoundID(1322))
}

actual object ShareKit {

    actual fun shareText(text: String) {
        val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
        val activityViewController = UIActivityViewController(listOf(text), null)
        currentViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}

@Composable
actual fun onThemeChange(themeType: ThemeType) {
}

actual object SpeechService {
    actual fun init(onStarted: () -> Unit, onEnded: () -> Unit) {
    }

    actual fun dispose(onStarted: () -> Unit, onEnded: () -> Unit) {
    }

    actual fun startTextToSpeech(voiceName: String, text: String) {
    }

    actual fun stopTextToSpeech() {
    }
}