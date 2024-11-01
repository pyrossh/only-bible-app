package dev.pyrossh.only_bible_app.utils

data class Pos(val start: Int, val end: Int)

interface Node {
    var pos: Pos
}

data class TextNode(override var pos: Pos, val value: String) : Node
data class TagNode(
    override var pos: Pos,
    val name: String,
    val attributes: Map<String, String> = emptyMap(),
    var child: TextNode? = null
) : Node {
    fun isSelfClosing() = name == "br"
}

class SimpleParser(val input: CharSequence) {
    var cursor: Int = 0

    private fun consume(): Char {
        return input[cursor++]
    }

    private fun peek(): Char {
        return input[cursor]
    }

    private fun skip() {
        cursor++
    }

    private fun skip(n: Int) {
        cursor += n
    }

    private fun hasNext(): Boolean {
        return cursor < input.length
    }

    fun parse(): List<Node> {
        val nodes = mutableListOf<Node>()
        while (hasNext()) {
            if (peek() != '<') {
                nodes.add(parseTextNode())
            } else {
                nodes.add(parseTag())
            }
        }
        return nodes
    }

    private fun parseTextNode(): TextNode {
        val start = cursor
        val result = StringBuilder()
        while (hasNext() && peek() != '<') {
            result.append(consume())
        }
        return TextNode(Pos(start, cursor), result.toString())
    }

    private fun parseTag(): TagNode {
        val start = cursor
        if (input.substring(start, start + 4) == "<br>") {
            skip(4)
            return TagNode(Pos(start, cursor), "br")
        }
        for (t in listOf("red", "yellow")) {
            if (input.substring(start, start + 2 + t.length) == "<$t>") {
                skip(t.length + 2)
                val textNode = parseTextNode()
                if (input.substring(textNode.pos.end, textNode.pos.end + t.length + 3) == "</$t>") {
                    skip(t.length + 3)
                    return TagNode(
                        Pos(start, textNode.pos.end + t.length + 3),
                        t,
                        emptyMap(),
                        textNode
                    )
                } else {
                    throw RuntimeException(
                        "failed find closing tag for <red> at ${textNode.pos.end} ${
                            input.substring(
                                textNode.pos.end,
                                textNode.pos.end + t.length + 3
                            )
                        }"
                    )
                }
            }
        }

        if (input.substring(start, start + 2) == "<a") {
            skip(2)
            val attrs = parseAttributes()
            if (consume() != '>') {
                throw RuntimeException("failed to parseTag 'a' close '>'")
            }
            val textNode = parseTextNode()
            if (input.substring(textNode.pos.end, textNode.pos.end + 4) == "</a>") {
                skip(4)
                return TagNode(Pos(start, textNode.pos.end + 4), "a", attrs, textNode)
            } else {
                throw RuntimeException(
                    "failed find closing tag for <a> ${textNode.pos.end}"
                )
            }
        }

        throw RuntimeException("failed to parseTag at $start ${input.substring(start, start + 10)}")
    }

    private fun parseAttributes(): Map<String, String> {
        val start = cursor
        val key = StringBuilder()
        while (hasNext() && peek() != '=') {
            while (peek() == ' ') {
                skip()
            }
            key.append(consume())
        }
        skip()
        while (peek() == ' ') {
            skip()
        }
        val value = parseString()
        if (peek() == '>') {
            return mapOf(Pair(key.toString(), value))
        }
        throw RuntimeException("failed to parseAttribute at $start")
    }

    private fun parseString(): String {
        val start = cursor
        val result = StringBuilder()
        if (consume() != '"') {
            throw RuntimeException("failed to parseAttribute at ${start + 1}")
        }

        while (hasNext() && peek() != '"') {
            result.append(consume())
        }
        skip()
        return result.toString()
    }
}