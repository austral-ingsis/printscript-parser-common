package org.austral.ingsis.printscript.common

interface TokenType {
    val type: String
    fun equals(type: TokenType): Boolean = this.type == type.type
}

data class Token(val type: TokenType, val from: Int, val to: Int, val range: LexicalRange)
data class LexicalRange(val startCol: Int, val startLine: Int, val endCol: Int, val endLine: Int)

enum class CoreTokenTypes(override val type: String): TokenType {
    EOF("EOF"),
    WHITESPACE("WHITESPACE"),
}
