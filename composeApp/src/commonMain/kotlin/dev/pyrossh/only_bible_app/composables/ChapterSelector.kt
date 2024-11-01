package dev.pyrossh.only_bible_app.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.pyrossh.only_bible_app.screens.ChapterScreenProps
import dev.pyrossh.only_bible_app.domain.Bible
import dev.pyrossh.only_bible_app.domain.chapterSizes
import dev.pyrossh.only_bible_app.domain.engTitles
import dev.pyrossh.only_bible_app.getScreenHeight
import utils.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSelector(
    bible: Bible,
    bookNames: List<String>,
    startBookIndex: Int,
    onClose: () -> Unit,
) {
    val navController = LocalNavController.current
    val height =  getScreenHeight() / 2
    var expanded by remember { mutableStateOf(false) }
    var bookIndex by remember { mutableIntStateOf(startBookIndex) }
    val scrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (startBookIndex - 2 >= 0)
            startBookIndex - 2
        else
            0,
    )
    Dialog(onDismissRequest = { onClose() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(8.dp),
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
//                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        expanded = !expanded
                    },
                colors = ListItemDefaults.colors(
                    containerColor = if (expanded)
                        MaterialTheme.colorScheme.surfaceContainer
                    else
                        MaterialTheme.colorScheme.background
                ),
                headlineContent = {
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
//                            .fillMaxWidth()
//                            .wrapContentWidth(Alignment.CenterHorizontally),
                        fontWeight = FontWeight.W600,
                        text = bookNames[bookIndex]
                    )
                },
                trailingContent = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            if (expanded) {
                LazyColumn(
                    state = scrollState,
                ) {
                    items(bookNames.filter { it != bookNames[bookIndex] }) {
                        ListItem(
                            modifier = Modifier.clickable {
                                bookIndex = bookNames.indexOf(it)
                                expanded = false
                            },
                            headlineContent = {
                                Text(
                                    modifier = Modifier.padding(start = 4.dp),
                                    fontWeight = FontWeight.W600,
                                    text = it,
                                )
                            },
                            supportingContent = {
                                if (bible.languageCode != "en") {
                                    Text(
                                        modifier = Modifier.padding(start = 4.dp),
                                        text = engTitles[bookNames.indexOf(it)],
                                    )
                                }
                            }
                        )
                    }
                }
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                columns = GridCells.Fixed(5)
            ) {
                items(chapterSizes[bookIndex]) { c ->
                    Surface(
                        shadowElevation = 1.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
//                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                onClose()
                                navController.navigate(
                                    ChapterScreenProps(
                                        bookIndex = bookIndex,
                                        chapterIndex = c,
                                        verseIndex = 0,
                                    )
                                )
                            }
                        ) {
                            Text(
                                fontWeight = FontWeight.W600,
                                text = "${c + 1}"
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
    }
}