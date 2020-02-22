package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.File

fun Char.isPunctuation(): Boolean = !(this.isLetterOrDigit() && this.isWhitespace())

class Parser {
    var appname = ""
    private var line = 0
    private var pos = 0
    var code: List<String> = listOf()
    val errors: MutableList<TangaraError> = mutableListOf()
    val tokens: MutableList<Token> = mutableListOf()
    val buffer: StringBuilder = StringBuilder()
    private val tc: TokensCreator = TokensCreator()
    private val new_code: MutableList<String> = mutableListOf()
    var platform: Platform = Platform()

    fun skipWhitespaces(): Boolean {
        new_code[line].trimStart()
        if (new_code[line].isEmpty()) {
            line++
            return if (line > code.size) {
                false
            } else {
                skipWhitespaces()
            }
        }
        return true
    }

    fun import(platformName: String) {
        platform = jacksonObjectMapper().readValue<Platform>(File("platforms/$platformName.json"))
    }

    fun parse() {
        while (skipWhitespaces()) {
            //pass
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }

    fun setCode(setting_code: Collection<String>) {
        code = setting_code.toList()
        new_code.clear()
        new_code.addAll(code)
    }
}