package dev.pyrossh.only_bible_app.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleParserTest {

    @Test
    fun parseTextOnly() {
        val parser = SimpleParser("lorem ipsum 123 dorem")
        assertEquals(
            listOf(TextNode(pos = Pos(0, 21), value = "lorem ipsum 123 dorem")),
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

    @Test
    fun parseTextA() {
        val parser =
            SimpleParser("The History of Creation <br> (<a href=\"42:0:0\">John 1:1–5</a> ; <a href=\"57:10:1\">Hebrews 11:1–3</a>)|In the beginning God created the heaven and the earth.")
        assertEquals(
            listOf(
                TextNode(pos = Pos(start = 0, end = 24), value = "The History of Creation "),
                TagNode(pos = Pos(start = 24, end = 28), name = "br"),
                TextNode(pos = Pos(start = 28, end = 30), value = " ("),
                TagNode(
                    pos = Pos(start = 30, end = 61),
                    name = "a",
                    attributes = mapOf("href" to "42:0:0"),
                    child = TextNode(pos = Pos(start = 47, end = 57), value = "John 1:1–5")
                ),
                TextNode(pos = Pos(start = 61, end = 64), value = " ; "),
                TagNode(
                    pos = Pos(start = 64, end = 100),
                    name = "a",
                    attributes = mapOf("href" to "57:10:1"),
                    child = TextNode(pos = Pos(start = 82, end = 96), value = "Hebrews 11:1–3")
                ),
                TextNode(
                    pos = Pos(start = 100, end = 156),
                    value = ")|In the beginning God created the heaven and the earth."
                ),
            ),
            parser.parse(),
        )
    }
}