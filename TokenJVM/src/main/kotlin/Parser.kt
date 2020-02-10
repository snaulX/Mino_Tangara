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
    val buffer: StringBuilder = StringBuilder()
    private val tc: TokensCreator = TokensCreator()
    private val current: Char
        get() {
            return code[line][pos]
        }
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

    fun parse() {
        var multiline_comment: Boolean = false
        tc.setOutput("$appname.tokens")
        tc.header = HeaderType.Script
        loop@ for (strline: String in code) {
            //pass
            if (buffer.isNotEmpty()) buffer.clear()
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                error(error.getError())
            }
        }
    }
}