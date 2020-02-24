package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.*
import java.lang.StringBuilder

class Parser {
    var appname = ""
    private var line = 1
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
    private val cur_lexem: String
        get() = lexemes[pos]

    infix fun import(platformName: String) {
        try {
            platform = jacksonObjectMapper().readValue<Platform>(File("platforms/$platformName.json"))
        }
        catch (e: FileNotFoundException) {
            errors.add(ImportError(line, "Platform by name $platformName not found. " +
                    "Platforms need saved in folder next to the compiler 'platforms/' " +
                    "and have extension .json"))
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

    fun lexerize() {
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

    fun checkExpressionEnd(onError: () -> Unit) {
        if (cur_lexem != platform.expression_end) onError
    }

    fun parseLexemes() {
        var security: SecurityDegree = SecurityDegree.PUBLIC
        var single_comment: Boolean = false
        pos = 0
        if (buffer.isNotEmpty()) buffer.clear()
        while (pos < lexemes.size) {
            if (single_comment) {
                if (cur_lexem == "\n") single_comment = false
            }
            else {
                with(platform) {
                    when (cur_lexem) {
                        import_keyword -> {
                            pos++
                            import(cur_lexem)
                            pos++
                            checkExpressionEnd {
                                errors.add(SyntaxError(line, "'$import_keyword' expression haven`t end"))
                            }
                        }
                        use_keyword -> {
                            pos++
                            while (Regex("""\w+\d*|\.""").matches(cur_lexem)) {
                                buffer.append(cur_lexem)
                                pos++
                            }
                            tc.importPackage(buffer.toString())
                            buffer.clear()
                            pos++
                            checkExpressionEnd {
                                errors.add(SyntaxError(line, "'$use_keyword' expression haven`t end"))
                            }
                        }
                        include_keyword -> {
                            pos++
                            while (Regex("""\w+\d*|\.|\\|/""").matches(cur_lexem)) {
                                buffer.append(cur_lexem)
                                pos++
                            }
                            tc.include(buffer.toString())
                            buffer.clear()
                            pos++
                            checkExpressionEnd {
                                errors.add(SyntaxError(line, "'$include_keyword' expression haven`t end"))
                            }
                        }
                        lib_keyword -> {
                            pos++
                            val local_path: Boolean = cur_lexem == "<"
                            if (local_path) {
                                buffer.append(cur_lexem)
                                pos++
                            }
                            while (Regex("""\w+\d*|\.|\|/""").matches(cur_lexem)) {
                                buffer.append(cur_lexem)
                                pos++
                            }
                            if (local_path) {
                                buffer.append(cur_lexem)
                                pos++
                            }
                            tc.linkLibrary(buffer.toString())
                            buffer.clear()
                            checkExpressionEnd {
                                errors.add(SyntaxError(line, "'$lib_keyword' expression haven`t end"))
                            }
                        }
                        single_comment_start -> single_comment = true
                        "\n" -> {
                            tc.incLine()
                            line++
                        }
                        else -> tc.callLiteral(cur_lexem)
                    }
                }
            }
            pos++
        }
    }

    fun parse() {
        tc.setOutput("$appname.tokens")
        lexerize()
        lexemes.forEach {
            print("$it, ")
        } //test: passed
        parseLexemes()
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }
}