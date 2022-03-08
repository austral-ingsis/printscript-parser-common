package org.austral.ingsis.printscript.common

import org.austral.ingsis.printscript.parser.Content
import org.austral.ingsis.printscript.parser.TokenIterator
import java.lang.Exception

data class TokenConsumeException(val got: TokenType, val expected: TokenType) : Exception("Expected: ${expected.type}, got: ${got.type}")

abstract class TokenConsumer(protected val stream: TokenIterator) {

    fun consume(type: TokenType): Content<String> = consume(type, StringRead)

    fun consume(type: TokenType, expected: String): Content<String> = consume(type, StringRead, expected)

    fun <T> consume(type: TokenType, read: Read<T>): Content<T> {
        val content = stream.peek(read)
        return if(content.token.type == type) stream.consume(read)
        else throw TokenConsumeException(current().type, type)
    }

    fun <T> consume(type: TokenType, read: Read<T>, expected: T): Content<T> {
        val content = peek(type, read)
        return if (content != null && content.content == expected) stream.consume(read)
        else throw TokenConsumeException(current().type, type)
    }

    fun consumeAny(vararg tokens: TokenType): Content<String> {
        val content = peekAny(*tokens)
        return if (content != null) consume(content.token.type, StringRead)
        else throw TokenConsumeException(current().type, tokens.first())
    }

    fun peek(type: TokenType): Content<String>? = peek(type, StringRead)
    fun peek(type: TokenType, expected: String): Content<String>? = peek(type, StringRead, expected)

    fun <T> peek(type: TokenType, read: Read<T>): Content<T>? {
        val content = stream.peek(read)
        return if(content.token.type == type) content
        else null
    }

    fun <T> peek(type: TokenType, read: Read<T>, expected: T): Content<T>? {
        val content = peek(type, read)
        return if (content != null && content.content == expected) content
        else null
    }

    fun peekAny(vararg tokens: TokenType): Content<String>? {
        val content = stream.peek(StringRead)
        val foundDesiredToken = tokens.any { content.token.type == it }
        return if(foundDesiredToken) content
        else null
    }

    fun <T> peek(read: Read<T>, vararg types: TokenType): List<Content<T>>? {
        val content = stream.peek(types.size, read)
        val zipped = content.zip(types)
        val allExpectedTokensAreInList = lazy { zipped.fold(true) { acc, curr -> acc && (curr.first.token.type == curr.second) } }
        return if(allExpectedTokensAreInList.value) content
        else null
    }

    fun current(): Token = stream.current()
}
