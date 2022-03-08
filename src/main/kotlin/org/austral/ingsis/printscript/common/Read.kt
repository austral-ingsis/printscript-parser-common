package org.austral.ingsis.printscript.common

interface Read<T> {
    fun read(content: String, from: Int, to: Int): T
    fun default(): T
}

object StringRead : Read<String> {
    override fun read(content: String, from: Int, to: Int): String {
        return content.substring(from, to)
    }

    override fun default(): String {
        return ""
    }
}

object IntRead : Read<Int> {
    override fun read(content: String, from: Int, to: Int): Int {
        return StringRead.read(content, from, to).toInt()
    }

    override fun default(): Int {
        return 0
    }
}
