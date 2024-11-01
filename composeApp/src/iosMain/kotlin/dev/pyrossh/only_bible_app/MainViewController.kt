package dev.pyrossh.only_bible_app

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.window.ComposeUIViewController
import com.russhwolf.settings.NSUserDefaultsSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter

val settings = NSUserDefaultsSettings.Factory().create()
val model = AppViewModel()

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController(configure = {
    delegate = object : ComposeUIViewControllerDelegate {
        override fun viewWillAppear(animated: Boolean) {
            super.viewWillAppear(animated)
            model.loadData(settings)
            NSNotificationCenter.defaultCenter.addObserver(this)
        }

        override fun viewWillDisappear(animated: Boolean) {
            super.viewWillDisappear(animated)
            model.saveData(settings)

        }
    }
}) {
    App(model = model)
}