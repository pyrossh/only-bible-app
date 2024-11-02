package dev.pyrossh.only_bible_app

import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings

val settings = NSUserDefaultsSettings.Factory().create()

fun MainViewController() = ComposeUIViewController() {
    App(settings = settings)
}