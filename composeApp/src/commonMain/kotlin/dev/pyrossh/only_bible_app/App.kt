package dev.pyrossh.only_bible_app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.Settings

@Composable
fun App(model: AppViewModel = viewModel { AppViewModel() }, settings: Settings) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    LaunchedEffect("") {
        lifeCycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                model.loadData(settings)
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                model.saveData(settings)
            }
        })
    }
    AppTheme(themeType = model.themeType) {
        AppHost(model = model)
    }
}