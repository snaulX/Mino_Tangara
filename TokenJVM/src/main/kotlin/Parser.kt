package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.File
import java.lang.StringBuilder

fun Char.isPunctuation(): Boolean = !(this.isJavaIdentifierPart() && this.isWhitespace())

class Parser {
    var appname = ""
    private var line = 0
    var code: String = ""
    val errors: MutableList<TangaraError> = mutableListOf()
    private val tc: TokensCreator = TokensCreator()
    var platform: Platform = Platform()
    val lexemes: MutableList<String> = mutableListOf()
    private val buffer: StringBuilder = StringBuilder()
    @ExperimentalUnsignedTypes
    private val pos: UInt = 1u
    private val cur: Char
        get() = code[pos.toInt()]

    infix fun import(platformName: String) {
        platform = jacksonObjectMapper().readValue<Platform>(File("platforms/$platformName.json"))
    }

    fun createLexemes() {
        /**
         * Check on empty and pushing to lexemes value from buffer and clear it
         */
        fun clearBuffer() {
            if (buffer.isNotEmpty()) {
                lexemes.add(buffer.toString())
                buffer.clear()
            }
        }

        while (pos <= code.length.toUInt()) {
            when {
                cur.isWhitespace() -> {
                    if (cur == '\n') tc.incLine()
                    clearBuffer()
                }
                cur.isPunctuation() -> {
                    clearBuffer()
                    lexemes.add(cur.toString())
                }
                cur.isJavaIdentifierPart() -> {
                    buffer.append(cur)
                } //for digits, letters and _
            }
            pos.inc() //pos++
        }
    }

    fun parseLexemes() {
        val ls: List<String> = lexemes.toList() //final lexemes
    }

    fun parse() {
        createLexemes()
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }

    fun checkSecurity(sec: String): SecurityDegree? {
        return when (sec) {
            "" -> SecurityDegree.PUBLIC
            platform.public_keyword -> SecurityDegree.PUBLIC
            platform.private_keyword -> SecurityDegree.PRIVATE
            platform.protected_keyword -> SecurityDegree.PROTECTED
            else -> {
                errors.add(SyntaxError(line, "Syntax of security is invalid"))
                return null
            }
        }
    }

    fun checkClassType(t: String): ClassType? {
        return when (t) {
            "" -> ClassType.DEFAULT
            platform.data_keyword -> ClassType.DATA
            platform.final_keyword -> ClassType.FINAL
            platform.static_keyword -> ClassType.STATIC
            platform.abstract_keyword -> ClassType.ABSTRACT
            platform.enum_keyword -> ClassType.ENUM
            else -> {
                errors.add(SyntaxError(line, "Syntax of class type is invalid"))
                return null
            }
        }
    }
}