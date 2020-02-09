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
            val keyword = readKeyword()
            if (keyword.isEmpty()) {
                if (current.isDigit()) {
                    readInteger()
                }
                else {
                    break
                }
            }
            if (keyword.startsWith(platform.directive_start)) {
                //it`s directive
                if (keyword == platform.directive_start) {
                    createError(SyntaxError(line, pos, "Directive haven`t name"))
                }
            }
            else if (keyword.startsWith(platform.annotaion_start)) {
                //it`s annotation
            }
            else if (keyword.startsWith(platform.statement_start)) {
                //it`s statement
            }
            else if (keyword.startsWith(platform.array_start)) {
                //it`s array
            }
            else if (keyword.startsWith(platform.string_char)) {
                //it`s string
            }
            else if (keyword.startsWith(platform.char_char)) {
                //it`s char
            }
            else if (keyword.startsWith(platform.single_comment)) {
                continue
            }
            else if (keyword.startsWith(platform.multiline_comment_start)) {
                multiline_comment = true
            }
            else if (keyword.contains(platform.multiline_comment_end) && multiline_comment) {
                multiline_comment = false
            }
            else {
                when (keyword) {
                    platform.import_keyword -> import()
                    platform.lib_keyword -> lib()
                    platform.use_keyword -> use()
                    platform.include_keyword -> include()
                    platform.switch_keyword -> switch()
                    platform.typeof_keyword -> addTypeof()
                    platform.expression_end -> continue@loop //thank you IntelliJ IDEA for '@loop'
                    platform.expression_separator -> createError(SyntaxError(line, pos, "Expression separator cannot be in start of line"))
                    else -> pushLiteral(buffer.toString())
                }
            }
            if (buffer.isNotEmpty()) buffer.clear()
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                error(error.getError())
            }
        }
    }

    fun use() = tc.importPackage(readKeyword())

    fun readInteger(): Int {
        buffer.clear()
        if (skipWhitespaces()) {
            while (current.isDigit()) {
                try {
                    buffer.append(current)
                    pos++
                } catch (e: Exception) {
                    break
                }
            }
        }
        return buffer.toString().toInt()
    }

    fun readDouble(): Double {
        buffer.clear()
        if (skipWhitespaces()) {
            while (current.isDigit() || current == platform.float_separator) {
                try {
                    buffer.append(current)
                    pos++
                } catch (e: Exception) {
                    break
                }
            }
        }
        return buffer.toString().toDouble()
    }

    fun readKeyword(): String {
        buffer.clear()
        if (skipWhitespaces()) {
            while (current.isLetter() || current == '_') {
                try {
                    buffer.append(current)
                    pos++
                } catch (e: Exception) {
                    break
                }
            }
        }
        return buffer.toString()
    }

    fun readKeywordInStatement(): String {
        buffer.clear()
        if (skipWhitespaces()) {
            if (has(platform.statement_start)) {
                val keyword = readKeyword()
                if (has(platform.statement_end)) {
                    return keyword
                }
                else {
                    createError(SyntaxError(line, pos, "Close statement expression is not valid"))
                }
            }
            else {
                createError(SyntaxError(line, pos, "Open statement expression is not valid"))
            }
        }
        return ""
    }

    fun readString(): String {
        buffer.clear()
        if (!skipWhitespaces()) {
            if (current == platform.string_char) {
                //So ok. Let`s parse string
                var slash = false
                while (current != platform.string_char && !slash) {
                    try {
                        slash = current == '\\'
                        buffer.append(current)
                        pos++
                    }
                    catch (e: IndexOutOfBoundsException) {
                        createError(SyntaxError(line, pos, "String hasn`t end"))
                        return ""
                    }
                }
                return buffer.toString()
            }
            else {
                createError(SyntaxError(line, pos, "$current is not string character"))
            }
        }
        return ""
    }

    fun readChar(): Char {
        buffer.clear()
        var result = ' '
        if (!skipWhitespaces()) {
            if (current == platform.char_char) {
                //So ok. Let`s parse char
                pos++
                if (current == '\\') {
                    pos++
                    buffer.append(current)
                    if (current == 'u') {
                        for (i: Int in 0..4) {
                            pos++
                            buffer.append(current)
                        }
                    }
                    TODO("Make char with slashes")
                }
                else result = current
                pos++
                if (current == platform.char_char) {
                    tc.loadValue(result)
                }
                else {
                    createError(SyntaxError(line, pos, "$current is not char character"))
                }
            }
            else {
                createError(SyntaxError(line, pos, "$current is not char character"))
            }
        }
        return result
    }

    private fun has(str: String): Boolean {
        if (!skipWhitespaces()) {
            for (i: Int in 0..str.length) {
                try {
                    if (current != str[i]) return false
                    pos++
                }
                catch (e: IndexOutOfBoundsException) {
                    return false
                }
            }
            return true
        }
        return false
    }

    fun getValue() {
        buffer.clear()
        if (!skipWhitespaces()) {
            if (current == platform.char_char) tc.loadValue(readChar())
            else if (current == platform.string_char) tc.loadValue(readString())
            else if (current.isDigit()) tc.loadValue(readInteger())
            else {
                while (true) {
                    if (has(platform.statement_start)) {
                        //it`s a method
                        parseMethod()
                        break
                    }
                    //pushLiteral()
                    if (current != platform.separator) {
                        break
                    }
                    pos++
                }
            }
        }
    }

    fun parseField(): String {
        var field_name = ""
        while (true) {
            field_name.plus(readKeyword())
            if (skipWhitespaces() && (current != platform.separator)) break
        }
        return field_name
    }

    fun parseMethod() {
        tc.callMethod(buffer.toString())
        var old_pos = pos
        while (!has(platform.statement_end)) {
            pos = old_pos
            getValue()
            old_pos = pos
        }
    }

    fun startBlock() {
        buffer.clear()
        if (skipWhitespaces()) {
            while (!current.isWhitespace()) {
                try {
                    buffer.append(current)
                    pos++
                } catch (e: Exception) {
                    break
                }
            }
            if (buffer.toString() == platform.block_start) {
                tc.startBlock()
            }
            else {
                createError(SyntaxError(line, pos, "'$buffer' is not start of block"))
            }
        }
    }

    fun endBlock() {
        if (skipWhitespaces()) {
            while (!current.isWhitespace()) {
                try {
                    buffer.append(current)
                    pos++
                } catch (e: Exception) {
                    break
                }
            }
            if (buffer.toString() == platform.block_end) {
                tc.endBlock()
            }
            else {
                createError(SyntaxError(line, pos, "'$buffer' is not end of block"))
            }
        }
    }

    fun import() {
        val platform_name = readKeyword()
        val mapper = jacksonObjectMapper()
        platform = mapper.readValue<Platform>(File("platforms/$platform_name.json"))
        code.add(0, platform.add_code)
        println("${platform.import_keyword} $platform")
    }

    fun lib() = tc.linkLibrary(readKeyword())

    fun createError(error: TangaraError) = errors.add(error)

    fun pushLiteral(literal: String) = tc.callLiteral(literal)

    fun include() = tc.include(readKeyword())

    fun switch() {
        if (has(platform.statement_start)) {
            val var_name = parseField()
            if (has(platform.statement_end)) {
                tc.createSwitch(var_name)
                startBlock()
                val caseKeyword = readKeyword()
                if (caseKeyword == platform.case_keyword) {
                    tc.createCase(readKeyword())
                }
                else {
                    createError(SyntaxError(line, pos, "$caseKeyword is not case keyword"))
                }
            }
        }
    }

    fun addTypeof() = tc.checkTypeof(readKeywordInStatement())
}