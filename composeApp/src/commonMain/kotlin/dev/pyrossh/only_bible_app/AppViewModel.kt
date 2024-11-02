package dev.pyrossh.only_bible_app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import dev.pyrossh.only_bible_app.domain.Bible
import dev.pyrossh.only_bible_app.domain.Verse
import dev.pyrossh.only_bible_app.domain.bibles
import dev.pyrossh.only_bible_app.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.ExperimentalResourceApi

class AppViewModel : ViewModel() {

    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<Exception?>(null)
    var isAudioPlaying by mutableStateOf(false)
    val verses = MutableStateFlow(listOf<Verse>())
    val bookNames = MutableStateFlow(listOf<String>())
    private val highlightedVerses = MutableStateFlow(JsonObject(mapOf()))

    var bible by mutableStateOf(bibles.first())
    var bookIndex by mutableIntStateOf(0)
    var chapterIndex by mutableIntStateOf(0)
    var verseIndex by mutableIntStateOf(0)
    var fontType by mutableStateOf(FontType.Sans)
    var fontSizeDelta by mutableIntStateOf(0)
    var fontBoldEnabled by mutableStateOf(false)
    var lineSpacingDelta by mutableIntStateOf(0)
    var themeType by mutableStateOf(ThemeType.Auto)
    val selectedVerses = MutableStateFlow(listOf<Verse>())
    val searchText = MutableStateFlow("")


    init {
        SpeechService.init({ isAudioPlaying = true }, { isAudioPlaying = false })
    }

    override fun onCleared() {
        super.onCleared()
        SpeechService.dispose({}, {})
    }

    @OptIn(FlowPreview::class)
    val searchedVerses = searchText.asStateFlow()
        .debounce(300)
        .combine(verses.asStateFlow()) { text, verses ->
            verses.filter { verse ->
                if (text.trim().isEmpty())
                    false
                else
                    verse.text.lowercase().contains(
                        text.trim().lowercase()
                    )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    fun onSearchTextChange(text: String) {
        searchText.value = text
    }

    fun setSelectedVerses(verses: List<Verse>) {
        selectedVerses.value = verses
    }

    fun clearSelectedVerses() {
        selectedVerses.value = listOf()
    }

    fun loadData(s: Settings) {
        println("loadData")
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.Main) {
                isLoading = true
            }
            val bibleFileName = s.getString("bible", "en_kjv")
            bookIndex = s.getInt("bookIndex", 0)
            chapterIndex = s.getInt("chapterIndex", 0)
            verseIndex = s.getInt("verseIndex", 0)
            fontType = FontType.valueOf(
                s.getString("fontType", FontType.Sans.name)
            )
            fontSizeDelta = s.getInt("fontSizeDelta", 0)
            fontBoldEnabled = s.getBoolean("fontBoldEnabled", false)
            lineSpacingDelta = s.getInt("lineSpacingDelta", 0)
            themeType = ThemeType.valueOf(
                s.getString("themeType", ThemeType.Auto.name)
            )
            highlightedVerses.value = Json.parseToJsonElement(
                s.getString("highlightedVerses", "{}")
            ).jsonObject
            val localBible = bibles.find { it.filename() == bibleFileName } ?: bibles.first()
            loadBible(localBible)
            viewModelScope.launch(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadBible(b: Bible) {
        println("loadBible")
        try {
            val buffer = Res.readBytes("files/${b.filename()}.txt").decodeToString()
            val localVerses = buffer.split("\n").filter { it.isNotEmpty() }.map {
                val arr = it.split("|")
                val bookName = arr[0]
                val book = arr[1].toInt()
                val chapter = arr[2].toInt()
                val verseNo = arr[3].toInt()
                val heading = arr[4]
                val verseText = arr.subList(5, arr.size).joinToString("|")
                Verse(
                    id = "${book}:${chapter}:${verseNo}",
                    bookIndex = book,
                    bookName = bookName,
                    chapterIndex = chapter,
                    verseIndex = verseNo,
                    heading = heading,
                    text = verseText,
                )
            }
            viewModelScope.launch(Dispatchers.Main) {
                bible = b
                verses.value = localVerses
                bookNames.value = localVerses.distinctBy { it.bookName }.map { it.bookName }
            }
        } catch (e: Exception) {
            println("-----------------------COULD NOT LOAD FILE")
            viewModelScope.launch(Dispatchers.Main) {
                isLoading = false
                error = e
            }
            e.printStackTrace()
        }
    }

    fun saveData(s: Settings) {
        println("saveData")
        viewModelScope.launch(Dispatchers.IO) {
            s.putString("bible", bible.filename())
            s.putInt("bookIndex", bookIndex)
            s.putInt("chapterIndex", chapterIndex)
            s.putInt("verseIndex", verseIndex)
            s.putString("fontType", fontType.name)
            s.putInt("fontSizeDelta", fontSizeDelta)
            s.putBoolean("fontBoldEnabled", fontBoldEnabled)
            s.putInt("lineSpacingDelta", lineSpacingDelta)
            s.putString("themeType", themeType.name)
            s.putString("highlightedVerses", highlightedVerses.value.toString())
        }
    }

    fun getHighlightForVerse(v: Verse): Int? {
        if (highlightedVerses.value.containsKey(v.id))
            return highlightedVerses.value[v.id]?.jsonPrimitive?.int
        return null
    }

    fun addHighlightedVerses(verses: List<Verse>, colorIndex: Int) {
        highlightedVerses.value = JsonObject(
            highlightedVerses.value + verses.associateBy({ it.id },
                { JsonPrimitive(colorIndex) })
        )
    }

    fun removeHighlightedVerses(verses: List<Verse>) {
        highlightedVerses.value = JsonObject(
            highlightedVerses.value.filterKeys { !verses.map { v -> v.id }.contains(it) }
        )
    }
}