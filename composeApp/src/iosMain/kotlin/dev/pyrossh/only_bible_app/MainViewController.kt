package dev.pyrossh.only_bible_app

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter

val settings = NSUserDefaultsSettings.Factory().create()
val model = AppViewModel()

fun MainViewController() = ComposeUIViewController() {
    App(model = model, settings = settings)
}