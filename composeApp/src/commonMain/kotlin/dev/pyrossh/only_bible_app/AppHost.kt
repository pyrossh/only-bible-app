package dev.pyrossh.only_bible_app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.pyrossh.only_bible_app.screens.ChapterScreen
import dev.pyrossh.only_bible_app.screens.ChapterScreenProps
import dev.pyrossh.only_bible_app.screens.Dir
import utils.LocalNavController

@Composable
fun AppHost(
    model: AppViewModel
) {
    val navController = rememberNavController()
    Text("Failed to load data")
    if (!model.isLoading) {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = navController,
                startDestination = ChapterScreenProps(
                    model.bookIndex,
                    model.chapterIndex,
                    model.verseIndex
                )
            ) {
                composable<ChapterScreenProps>(
                    enterTransition = {
                        val props = this.targetState.toRoute<ChapterScreenProps>()
                        slideIntoContainer(
                            Dir.valueOf(props.dir).slideDirection(),
                            tween(400),
                        )
                    },
                    exitTransition = {
                        ExitTransition.None
                    },
                    popEnterTransition = {
                        EnterTransition.None
                    },
                    popExitTransition = {
                        val props = this.targetState.toRoute<ChapterScreenProps>()
                        slideOutOfContainer(
                            Dir.valueOf(props.dir).reverse().slideDirection(),
                            tween(400),
                        )
                    }
                ) {
                    val props = it.toRoute<ChapterScreenProps>()
                    ChapterScreen(
                        model = model,
                        bookIndex = props.bookIndex,
                        chapterIndex = props.chapterIndex,
                        verseIndex = props.verseIndex,
                    )
                }
            }
        }
    }
}