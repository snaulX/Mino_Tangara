package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*

fun Char.isPunctuation(): Boolean = !(this.isLetterOrDigit() && this.isWhitespace())

class Parser {
    var appname = ""
    private var line = 0
    private var pos = 0
    val code: MutableList<String> = mutableListOf()
    val errors: MutableList<TangaraError> = mutableListOf()
    val tokens: MutableList<Token> = mutableListOf()
    val buffer: StringBuilder = StringBuilder()
    private val tc: TokensCreator = TokensCreator()
    private val current: Char
        get() = code[line][pos]
    var platform: Platform = Platform()

    fun skipWhitespaces(): Boolean {
        while (current.isWhitespace())
        {
            try {
                pos++
            }
            catch (e: IndexOutOfBoundsException) {
                try {
                    line++
                }
                catch (e: IndexOutOfBoundsException) {
                    return false
                }
            }
        }
        return true
    }

    fun import(platformName: String) {
        //pass
    }

    fun read(word: String): Boolean {
        val old_pos = pos
        for (i in 0..word.length) {
            pos++
            if (current != word[i]) {
                pos = old_pos
                return false
            }
        }
        return true
    }

    fun parse() {
        if (skipWhitespaces()) {
            with(platform) {
                when (current) {
                    import_keyword[0] -> {
                        if (read(import_keyword)) {
                            //so good
                        }
                    }
                }
            }
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }
}