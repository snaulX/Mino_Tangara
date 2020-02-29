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

    fun checkExpressionEnd(curLexem: String, onNotExpression: () -> Unit, onTrueExpression: () -> Unit = {}) {
        val i = curLexem.indexOf(platform.expression_end)
        if (i >= 0) onTrueExpression
        else onNotExpression
    }

    fun lexerize() {
        var singleComment: Boolean = false
        var multiComment: Boolean = false
        var hasString: Boolean = false
        var isLib: Boolean = false
        var isImport: Boolean = false
        var isUse: Boolean = false
        var isInclude: Boolean = false
        var isClass: Boolean = false //is initilization of class
        var isVar: Boolean = false
        var isFunction: Boolean = false
        var security: SecurityDegree = SecurityDegree.PUBLIC
        var identifer: Identifer = Identifer.DEFAULT
        var name: String = ""
        var levelStatement: Byte = 0 //level of statement
        var levelBlock: Byte = 0 //level of block

        /**
         * Push [lexem] to TokensCreator
         */
        fun parseLexem(lexem: String) {
            when {
                singleComment -> {
                    if (lexem == "\n") singleComment = false
                }
                multiComment -> {
                    if (lexem == platform.multiline_comment_end) multiComment = false
                }
                isImport -> {
                    val i = lexem.indexOf(platform.expression_end)
                    if (i >= 0) import(Regex("""(\w+\d*)""").find(lexem)!!.destructured.component1())
                    isImport = false
                    println(platform.import_keyword)
                }
                isInclude -> {
                    checkExpressionEnd(lexem, {
                        buffer.append(Regex("""(\w+\d*|\.|\\|/)""").find(lexem)!!.destructured.component1())
                    }, {
                        tc.include(buffer.toString())
                        buffer.clear()
                        isInclude = false
                    })
                }
                isUse -> {
                    checkExpressionEnd(lexem, {
                        buffer.append(Regex("""(\w+\d*|\.)""").find(lexem)!!.destructured.component1())
                    }, {
                        tc.importPackage(buffer.toString())
                        buffer.clear()
                        isUse = false
                    })
                }
                isLib -> {
                    checkExpressionEnd(lexem, {
                        buffer.append(Regex("""(\w+\d*|\.|\\|/)""").find(lexem)!!.destructured.component1())
                    }, {
                        tc.linkLibrary(buffer.toString())
                        buffer.clear()
                        isLib = false
                    })
                }
                isClass -> {
                    if (name.isEmpty()) name = lexem
                    isClass = false
                }
                else -> {
                    platform.run {
                        when (lexem) {
                            import_keyword -> isImport = true
                            single_comment_start -> singleComment = true
                            multiline_comment_start -> multiComment = true
                            include_keyword -> isInclude = true
                            use_keyword -> isUse = true
                            lib_keyword -> isLib = true
                            public_keyword -> security = SecurityDegree.PUBLIC
                            private_keyword -> security = SecurityDegree.PRIVATE
                            protected_keyword -> security = SecurityDegree.PROTECTED
                            final_keyword -> identifer = Identifer.FINAL
                            data_keyword -> identifer = Identifer.DATA
                            enum_keyword -> identifer = Identifer.ENUM
                            static_keyword -> identifer = Identifer.STATIC
                            abstract_keyword -> identifer = Identifer.ABSTRACT
                            class_keyword -> isClass = true
                            statement_start -> levelStatement++
                            statement_end -> levelStatement--
                            block_start -> levelBlock++
                            block_end -> levelBlock--
                            else -> tc.callLiteral(lexem)
                        }
                    }
                }
            }
        }
        /**
         * Check on empty and pushing to lexemes value from buffer and clear it
         */
        fun clearBuffer() {
            if (buffer.isNotEmpty()) {
                val buf: String = buffer.toString()
                if (buf.contains(platform.string_char)) {
                    val ind: Int = buf.indexOf(platform.string_char)
                    if (!(hasString && buf[ind - 1] == '\\'))
                        hasString = !hasString
                }
                parseLexem(buf)
                buffer.clear()
            }
        }

        while (pos < code.length) {
            when {
                hasString -> {
                    buffer.append(cur)
                }
                cur.isWhitespace() -> {
                    clearBuffer()
                    if (cur == '\n') {
                        tc.incLine()
                        parseLexem("\n")
                    } //for single line comments and if end_expression is nothing
                }
                cur.isJavaIdentifierPart() -> {
                    if (!prev.isJavaIdentifierPart())  clearBuffer()
                    buffer.append(cur)
                } //for digits, letters and _
                else -> {
                    if (prev.isWhitespace() || prev.isJavaIdentifierPart()) clearBuffer()
                    platform::javaClass.invoke().fields
                        .filter { it.get(platform).toString() == buffer.toString() }
                        .forEach { clearBuffer() }  //TODO("Повысить производительность")
                    buffer.append(cur)
                } //punctuation
            }
            pos++
        }
        clearBuffer()
    }

    fun parse() {
        tc.setOutput("$appname.tokens")
        lexerize()
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }
}