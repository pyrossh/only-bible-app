package dev.pyrossh.only_bible_app.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleParserTest {

    @Test
    fun parseTextOnly() {
        val parser = SimpleParser("lorem ipsum 123 dorem")
        assertEquals(
            listOf(TextNode(pos = Pos(0, 12), value = "lorem ipsum 123 dorem")),
            parser.parse(),
        )
    }

    @Test
    fun parseTextWithBr() {
        val parser = SimpleParser("lorem ipsum <red>dorum</red> data 123")
        assertEquals(
            listOf(
                TextNode(pos = Pos(0, 12), value = "lorem ipsum "),
                TagNode(
                    pos = Pos(start = 12, end = 28),
                    name = "red",
                    child = TextNode(pos = Pos(start = 17, end = 22), value = "dorum")
                ),
                TextNode(pos = Pos(start = 28, end = 37), value = " data 123"),
            ),
            parser.parse(),
        )
    }
}