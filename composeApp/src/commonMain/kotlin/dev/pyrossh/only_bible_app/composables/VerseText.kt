package dev.pyrossh.only_bible_app.composables

import dev.pyrossh.only_bible_app.AppViewModel
import dev.pyrossh.only_bible_app.screens.ChapterScreenProps
import dev.pyrossh.only_bible_app.FontType
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import dev.pyrossh.only_bible_app.ShareKit
import dev.pyrossh.only_bible_app.SpeechService
import dev.pyrossh.only_bible_app.darkHighlights
import dev.pyrossh.only_bible_app.domain.Verse
import dev.pyrossh.only_bible_app.isLightTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import dev.pyrossh.only_bible_app.lightHighlights
import dev.pyrossh.only_bible_app.utils.SimpleParser
import dev.pyrossh.only_bible_app.utils.TagNode
import dev.pyrossh.only_bible_app.utils.TextNode
import utils.LocalNavController

@Composable
fun VerseText(
    model: AppViewModel,
    fontType: FontType,
    fontSizeDelta: Int,
    fontBoldEnabled: Boolean,
    verse: Verse,
    highlightWord: String?,
) {
    var barYPosition by remember {
        mutableIntStateOf(0)
    }
    val selectedVerses by model.selectedVerses.collectAsState()
    val isLight = isLightTheme(model.themeType, isSystemInDarkTheme())
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isSelected = selectedVerses.contains(verse)
    val highlightedColorIndex = model.getHighlightForVerse(verse)
    val currentHighlightColors = if (isLight) lightHighlights else darkHighlights
    val text = if (highlightWord != null)
        verse.text.replace(
            highlightWord,
            "<yellow>${highlightWord}</yellow>",
            true
        )
    else
        verse.text
    val nodes = SimpleParser(text).parse()
    Text(
        modifier = Modifier
            .onPlaced {
                barYPosition = it.positionInRoot().y.toInt() + it.size.height
            }
            .clickable(
                interactionSource = buttonInteractionSource,
                indication = null
            ) {
                model.setSelectedVerses(
                    if (selectedVerses.contains(verse)) {
                        selectedVerses - verse
                    } else {
                        selectedVerses + verse
                    }
                )
            },
        style = TextStyle(
            background = if (isSelected)
                MaterialTheme.colorScheme.outlineVariant
            else
                if (highlightedColorIndex != null && isLight)
                    currentHighlightColors[highlightedColorIndex]
                else
                    Color.Unspecified,
            fontFamily = fontType.family(),
            color = if (isLight)
                Color(0xFF000104)
            else
                if (highlightedColorIndex != null)
                    currentHighlightColors[highlightedColorIndex]
                else
                    Color(0xFFBCBCBC),
            fontWeight = if (fontBoldEnabled)
                FontWeight.W700
            else
                FontWeight.W400,
            fontSize = (17 + fontSizeDelta).sp,
            lineHeight = (23 + fontSizeDelta).sp,
            letterSpacing = 0.sp,
        ),
        text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = (13 + fontSizeDelta).sp,
                    color = if (isLight)
                        Color(0xFFA20101)
                    else
                        Color(0xFFCCCCCC),
                    fontWeight = FontWeight.W700,
                )
            ) {
                append("${verse.verseIndex + 1} ")
            }
            for (n in nodes) {
                if (n is TextNode) {
                    append(n.value)
                } else if (n is TagNode && n.name == "br") {
                    append("\n")
                } else if (n is TagNode && n.name == "red") {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                        )
                    ) {
                        append(n.child!!.value)
                    }
                } else if (n is TagNode && n.name == "yellow") {
                    withStyle(
                        style = SpanStyle(
                            background = Color.Yellow,
                        )
                    ) {
                        append(n.child!!.value)
                    }
                }
            }
        }
    )
    if (isSelected && selectedVerses.last() == verse) {
        Menu(
            model = model,
            barYPosition = barYPosition,
            verse = verse,
            highlightWord = highlightWord,
        )
    }
}

@Composable
private fun Menu(
    model: AppViewModel,
    barYPosition: Int,
    verse: Verse,
    highlightWord: String?,
) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val selectedVerses by model.selectedVerses.collectAsState()
    Popup(
        alignment = Alignment.TopCenter,
        offset = IntOffset(0, y = barYPosition),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 0.5.dp,
            tonalElevation = 0.5.dp,
            modifier = Modifier
                .width(if (highlightWord != null) 360.dp else 300.dp)
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .shadow(
                    elevation = 2.dp,
                    spotColor = MaterialTheme.colorScheme.outline,
                    ambientColor = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {
//                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    model.removeHighlightedVerses(selectedVerses)
                    model.setSelectedVerses(listOf())
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = "Clear",
                    )
                }
                lightHighlights.forEachIndexed { i, tint ->
                    IconButton(onClick = {
//                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        model.addHighlightedVerses(selectedVerses, i)
                        model.setSelectedVerses(listOf())
                    }) {
                        Icon(
                            modifier = Modifier
                                .size(20.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            imageVector = Icons.Filled.Circle,
                            contentDescription = "highlight",
                            tint = tint,
                        )
                    }
                }
                IconButton(onClick = {
//                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    if (model.isAudioPlaying) {
                        SpeechService.stopTextToSpeech()
                    } else {
                        scope.launch(Dispatchers.IO) {
                            for (v in selectedVerses.sortedBy { it.verseIndex }) {
                                SpeechService.startTextToSpeech(model.bible.voiceName, v.text)
                            }
                        }
                    }
                }) {
                    Icon(
//                            modifier = Modifier.size(36.dp),
                        imageVector = if (model.isAudioPlaying)
                            Icons.Outlined.PauseCircle
                        else
                            Icons.Outlined.PlayCircle,
                        contentDescription = "Audio",
                    )
                }
                IconButton(onClick = {
//                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    val verses = selectedVerses.sortedBy { it.verseIndex }
                    val versesThrough =
                        if (verses.size >= 3) "${verses.first().verseIndex + 1}-${verses.last().verseIndex + 1}" else verses.map { it.verseIndex + 1 }
                            .joinToString(",")
                    val title =
                        "${verses[0].bookName} ${verses[0].chapterIndex + 1}:${versesThrough}"
                    val text = verses.joinToString("\n") {
                        it.text.replace("<red>", "")
                            .replace("</red>", "")
                    }
                    ShareKit.shareText("$title\n$text")
                    model.setSelectedVerses(listOf())
                }) {
                    Icon(
//                            modifier = Modifier.size(32.dp),
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                    )
                }
                if (highlightWord != null) {
                    IconButton(onClick = {
//                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        navController.navigate(
                            ChapterScreenProps(
                                bookIndex = verse.bookIndex,
                                chapterIndex = verse.chapterIndex,
                                verseIndex = verse.verseIndex,
                            )
                        )
                    }) {
                        Icon(
//                            modifier = Modifier.size(32.dp),
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = "Goto",
                        )
                    }
                }
            }
        }
    }
}