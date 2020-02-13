package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.File

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

    fun getToken(): Token {
        return Token()
    }

    fun parse() {
        var multiline_comment: Boolean = false
        tc.header = HeaderType.Script
        tc.setOutput("$appname.tokens")
        loop@ for (strline: String in code) {
            tokens.add(getToken())
            if (buffer.isNotEmpty()) buffer.clear()
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                error(error.getError())
            }
        }
    }
}