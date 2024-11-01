package dev.pyrossh.only_bible_app.utils

class SimpleParser(val input: CharSequence) {
    var cursor: Int = 0

    private fun step(): Char {
        return input[cursor++]
    }

    private fun peek(): Char {
        return input[cursor]
    }

    private fun skip() {
        cursor++
    }

    private fun hasNext(): Boolean {
        return cursor < input.length
    }

    fun read(predicate: (Char) -> Boolean): String {
        val result = StringBuilder()
        while (predicate(peek())) {
            result.append(step())
        }
        return result.toString()
    }

    fun read(pattern: CharSequence): String {
        val result = StringBuilder()
        var patternCursor = 0
        while (peek() == pattern[patternCursor++]) {
            result.append(skip())
        }
        return result.toString()
    }
}