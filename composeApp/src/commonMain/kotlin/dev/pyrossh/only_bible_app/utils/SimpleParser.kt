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
        for (t in listOf("red", "yellow", "em")) {
            if (input.substring(start, start + 2 + t.length) == "<$t>") {
                val redNode = TagNode(Pos(start, 0), t)
                skip(t.length + 2)
                val textNode = parseTextNode()
                if (input.substring(textNode.pos.end, textNode.pos.end + t.length + 3) == "</$t>") {
                    skip(t.length + 3)
                    redNode.pos = Pos(start, textNode.pos.end + t.length + 3)
                    redNode.child = textNode
                    return redNode
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

        throw RuntimeException("failed to parseTag at $start ${input.substring(start, start + 10)}")
    }
}