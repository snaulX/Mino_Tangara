package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.File
import java.lang.StringBuilder

class Parser {
    var appname = ""
    private var line = 0
    var code: String = ""
    val errors: MutableList<TangaraError> = mutableListOf()
    private val tc: TokensCreator = TokensCreator()
    var platform: Platform = Platform()
    val lexemes: MutableList<String> = mutableListOf()
    private val buffer: StringBuilder = StringBuilder()
    private var pos: Int = 0
    private val cur: Char
        get() = code[pos]
    private val prev: Char
        get() {
            return try {
                code[pos - 1]
            }
            catch (e: StringIndexOutOfBoundsException) {
                ' '
            }
        }

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

        while (pos < code.length) {
            when {
                cur.isWhitespace() -> {
                    if (cur == '\n') lexemes.add("\n") //for single line comments and if end_expression is nothing
                    clearBuffer()
                }
                cur.isJavaIdentifierPart() -> {
                    if (!prev.isJavaIdentifierPart())  clearBuffer()
                    buffer.append(cur)
                } //for digits, letters and _
                else -> {
                    if (prev.isWhitespace() || prev.isJavaIdentifierPart()) clearBuffer()
                    buffer.append(cur)
                } //punctuation
            }
            pos++
        }
        clearBuffer()
    }

    fun parseLexemes() {
        val ls: List<String> = lexemes.toList() //final lexemes
    }

    fun parse() {
        tc.setOutput("$appname.tokens")
        createLexemes()
        lexemes.forEach {
            print("$it, ")
        } //test: passed
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