package dev.pyrossh.only_bible_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val model by viewModels<AppViewModel>()
    private val settings by lazy {
        val prefs = applicationContext.getSharedPreferences("data", MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ShareKit.setActivityProvider { return@setActivityProvider this }
        if (savedInstanceState == null) {
            lifecycleScope.launch {
                model.loadData(settings)
            }
        }
        setContent {
            App(model = model)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        lifecycleScope.launch {
            model.saveData(settings)
        }
    }
}

