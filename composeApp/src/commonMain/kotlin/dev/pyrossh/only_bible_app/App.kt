package dev.pyrossh.only_bible_app

import androidx.compose.runtime.Composable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.Settings

@Composable
fun App(model: AppViewModel = viewModel { AppViewModel() }, settings: Settings) {
    LocalLifecycleOwner.current.lifecycle.addObserver(object : DefaultLifecycleObserver  {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            model.loadData(settings)
        }
        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            model.saveData(settings)
        }
    })
    AppTheme(themeType = model.themeType) {
        AppHost(model = model)
    }
}