package org.austral.ingsis.printscript.parser

import org.austral.ingsis.printscript.common.*
import org.austral.ingsis.printscript.common.CoreTokenTypes.*

data class Content<T>(val content: T, val token: Token)

class TokenIterator private constructor(private val content: String, private val tokens: List<Token>) {

    private var offset = 0

    companion object {
        private val EOF_TOKEN = Token(EOF, 0, 0, LexicalRange(0, 0, 0, 0))
        private fun <T> consumedEofToken(read: Read<T>) = Content(read.default(), EOF_TOKEN)

        fun create(content: String, tokens: List<Token>) = TokenIterator(content, tokens.filter { it.type != WHITESPACE })
    }

    fun <T> peek(read: Read<T>): Content<T> {
        return if (hasNext()) {
            val token = tokens[offset]
            content(token, read)
        } else consumedEofToken(read)
    }

    fun <T> peek(amount: Int, read: Read<T>): List<Content<T>> {
        val amountLeft = tokens.size - offset
        return if (amountLeft < amount) tokens.subList(offset, tokens.size).map { token -> content(token, read) }
        else tokens.subList(offset, offset + amount).map { token -> content(token, read) }
    }

    fun <T> consume(read: Read<T>): Content<T> {
        return if (hasNext()) {
            val token = tokens[offset]
            advance()
            content(token, read)
        } else consumedEofToken(read)
    }

    fun hasNext(): Boolean = offset < tokens.size

    fun current(): Token = if (hasNext()) tokens[offset] else EOF_TOKEN

    private fun <T> content(token: Token, read: Read<T>): Content<T> {
        return Content(read.read(content, token.from, token.to), token)
    }

    private fun advance() {
        offset += 1
    }
}
