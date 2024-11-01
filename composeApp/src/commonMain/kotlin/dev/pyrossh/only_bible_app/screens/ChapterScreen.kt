package dev.pyrossh.only_bible_app.screens
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ButtonDefaults.ContentPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pyrossh.only_bible_app.AppViewModel
import dev.pyrossh.only_bible_app.composables.BibleSelector
import dev.pyrossh.only_bible_app.composables.ChapterSelector
import dev.pyrossh.only_bible_app.composables.EmbeddedSearchBar
import dev.pyrossh.only_bible_app.composables.TextSettingsBottomSheet
import dev.pyrossh.only_bible_app.composables.VerseHeading
import dev.pyrossh.only_bible_app.composables.VerseText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import utils.LocalNavController
import dev.pyrossh.only_bible_app.utils.detectSwipe
import utils.getBackwardPair
import utils.getForwardPair

@Serializable
data class ChapterScreenProps(
    val bookIndex: Int,
    val chapterIndex: Int,
    val verseIndex: Int,
    val dir: String = Dir.Left.name,
)

enum class Dir {
    Left, Right;

    fun slideDirection(): AnimatedContentTransitionScope.SlideDirection {
        return when (this) {
            Left -> AnimatedContentTransitionScope.SlideDirection.Left
            Right -> AnimatedContentTransitionScope.SlideDirection.Right
        }
    }

    fun reverse(): Dir {
        return if (this == Left) Right else Left
    }
}


@Composable
fun ChapterScreen(
    model: AppViewModel,
    bookIndex: Int,
    chapterIndex: Int,
    verseIndex: Int,
) {
//    val view = LocalView.current
//    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    var isSettingsShown by remember { mutableStateOf(false) }
    var isSearchShown by remember { mutableStateOf(false) }
    var chapterSelectorShown by remember { mutableStateOf(false) }
    var bibleSelectorShown by remember { mutableStateOf(false) }
    val bookNames by model.bookNames.collectAsState()
    val verses by model.verses.collectAsState()
    val state = rememberSaveable(saver = listSaver(
        save = {
            model.verseIndex = it.firstVisibleItemIndex
            listOf(it.firstVisibleItemIndex, it.firstVisibleItemScrollOffset)
        },
        restore = {
            LazyListState(
                firstVisibleItemIndex = model.verseIndex,
            )
        }
    )) {
        LazyListState(
            firstVisibleItemIndex = verseIndex,
        )
    }
    val chapterVerses =
        verses.filter { it.bookIndex == bookIndex && it.chapterIndex == chapterIndex }
    LaunchedEffect(Unit) {
        model.clearSelectedVerses()
        model.bookIndex = bookIndex
        model.chapterIndex = chapterIndex
        model.verseIndex = verseIndex
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        if (bibleSelectorShown) {
            BibleSelector(
                bible = model.bible,
                onSelected = {
//                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    bibleSelectorShown = false
                    scope.launch(Dispatchers.IO) {
                        model.loadBible(it)
                    }
                },
                onClose = { bibleSelectorShown = false },
            )
        }
        if (chapterSelectorShown) {
            ChapterSelector(
                bible = model.bible,
                bookNames = bookNames,
                startBookIndex = bookIndex,
                onClose = { chapterSelectorShown = false },
            )
        }
        if (isSearchShown) {
            EmbeddedSearchBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                model = model,
                onDismiss = { isSearchShown = false },
            )
        }

        if (isSettingsShown) {
            TextSettingsBottomSheet(
                model = model,
                onDismiss = { isSettingsShown = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                TextButton(
                    contentPadding = PaddingValues(
                        top = ContentPadding.calculateTopPadding(), //8dp
                        end = 12.dp,
                        bottom = ContentPadding.calculateBottomPadding()
                    ),
                    onClick = {
//                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        chapterSelectorShown = true
                    }
                ) {
                    Text(
                        text = "${bookNames[bookIndex]}   ${chapterIndex + 1}",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.W500,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(
                        onClick = {
//                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            model.clearSelectedVerses()
                            isSearchShown = true
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    TextButton(onClick = {
//                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        bibleSelectorShown = true
                    }) {
                        Text(
                            text = model.bible.shortName(),
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W500,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                    TextButton(
                        onClick = {
//                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            isSettingsShown = true
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            LazyColumn(
                state = state,
                verticalArrangement = Arrangement.spacedBy(16.dp + (model.lineSpacingDelta * 2).dp),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectSwipe(
                            onSwipeLeft = {
                                val pair = getForwardPair(bookIndex, chapterIndex)
                                navController.navigate(
                                    ChapterScreenProps(
                                        bookIndex = pair.first,
                                        chapterIndex = pair.second,
                                        verseIndex = 0,
                                    )
                                )
                            },
                            onSwipeRight = {
                                val pair = getBackwardPair(bookIndex, chapterIndex)
                                navController.navigate(
                                    ChapterScreenProps(
                                        bookIndex = pair.first,
                                        chapterIndex = pair.second,
                                        verseIndex = 0,
                                        dir = Dir.Right.name,
                                    )
                                )
                            },
                        )
                    }
            ) {
                items(chapterVerses) { v ->
                    if (v.heading.isNotEmpty()) {
                        VerseHeading(
                            text = v.heading,
                            fontType = model.fontType,
                            fontSizeDelta = model.fontSizeDelta,
                        )
                    }
                    VerseText(
                        model = model,
                        fontType = model.fontType,
                        fontSizeDelta = model.fontSizeDelta,
                        fontBoldEnabled = model.fontBoldEnabled,
                        verse = v,
                        highlightWord = null,
                    )
                }
            }
        }
    }
}
