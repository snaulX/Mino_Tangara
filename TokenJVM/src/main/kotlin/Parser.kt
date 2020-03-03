package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import com.fasterxml.jackson.module.kotlin.*
import java.io.*
import java.lang.StringBuilder
import kotlin.reflect.full.memberProperties

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
    private val punctation_keywords: MutableList<String> = mutableListOf()

    fun checkPunctuation() {
        punctation_keywords.clear()
        platform::class.memberProperties.forEach {
            val keyword = it.call(platform).toString()
            if (keyword.isPunctuation()) {
                punctation_keywords.add(keyword)
            }
        }
    }

    infix fun import(platformName: String) {
        try {
            platform = jacksonObjectMapper().readValue(File("platforms/$platformName.json"))
            checkPunctuation()
        }
        catch (e: FileNotFoundException) {
            errors.add(
                ImportError(
                    line, "Platform by name $platformName not found. " +
                            "Platforms need saved in folder next to the compiler 'platforms/' " +
                            "and have extension .json"
                )
            )
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
        var isClass: Boolean = false
        var isVar: Boolean = false
        var isFunction: Boolean = false
        var isGoto: Boolean = false
        var isTypeAlias: Boolean = false
        var isFuncAlias: Boolean = false
        var needEnd: Boolean = false //check for need of end of expression
        var security: SecurityDegree = SecurityDegree.PUBLIC
        var identifer: Identifer = Identifer.DEFAULT
        var typeName: String = ""

        /**
         * Push [lexem] to TokensCreator
         */
        fun parseLexem(lexem: String) {
            println(lexem)
            when {
                singleComment -> {
                    if (lexem == "\n") singleComment = false
                }
                multiComment -> {
                    if (lexem == platform.multiline_comment_end) multiComment = false
                }
                needEnd -> {
                    needEnd = if (lexem != platform.expression_end) {
                        if (lexem == "\n") true
                        else {
                            errors.add(
                                SyntaxError(
                                    line, "End of expression not found. Invalid end is $lexem"
                                )
                            )
                            false
                        }
                    }
                    else false
                }
                isImport -> {
                    try {
                        import(Regex("""(\w+\d*)""").find(lexem)!!.destructured.component1())
                    } catch (e: KotlinNullPointerException) {
                        errors.add(ImportError(line, "$lexem is not valid platform name"))
                    }
                    isImport = false
                    needEnd = true
                }
                isInclude -> {
                    checkExpressionEnd(lexem, {
                        try {
                            buffer.append(Regex("""(\w+\d*|\.|\\|/)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(IncludeError(line, "$lexem is not valid incuding library name"))
                        }
                    }, {
                        tc.include(buffer.toString())
                        buffer.clear()
                        isInclude = false
                    })
                }
                isUse -> {
                    checkExpressionEnd(lexem, {
                        try {
                            buffer.append(Regex("""(\w+\d*|\.)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(UseError(line, "$lexem is not valid using package (namespace) name"))
                        }
                    }, {
                        tc.importPackage(buffer.toString())
                        buffer.clear()
                        isUse = false
                    })
                }
                isLib -> {
                    checkExpressionEnd(lexem, {
                        try {
                            buffer.append(Regex("""(\w+\d*|\.|\\|/)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(LibError(line, "$lexem is not valid tokens library name"))
                        }
                    }, {
                        tc.linkLibrary(buffer.toString())
                        buffer.clear()
                        isLib = false
                    })
                }
                isClass -> {
                    if (Regex("""\w+""").matches(lexem))
                        tc.createClass(lexem, security, identifer.classType)
                    else
                        errors.add(SyntaxError(line, "$lexem is not valid name of class"))
                    isClass = false
                }
                isFunction -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        val ft: FuncType? = identifer.funcType
                        if (ft == null) {
                            errors.add(SyntaxError(line, "Invalid type of function"))
                        }
                        else {
                            tc.createMethod(lexem, typeName, security, ft)
                        }
                        isFunction = false
                    }
                }
                isVar -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        when (identifer) {
                            Identifer.STATIC -> tc.createStaticField(lexem, typeName, security)
                            Identifer.FINAL -> tc.createFinalField(lexem, typeName, security)
                            Identifer.DEFAULT -> tc.createField(lexem, typeName, security)
                            else -> errors.add(SyntaxError(line, "Invalid type of variable"))
                        }
                        isVar = false
                    }
                }
                isTypeAlias -> {
                    //tc.createTypeAlias(lexem)
                    isTypeAlias = false
                }
                isFuncAlias -> {
                    //tc.createFuncAlias(lexem)
                    isFuncAlias = false
                }
                isGoto -> {
                    tc.goto(lexem)
                    isGoto = false
                    needEnd = true
                }
                identifer == Identifer.ENUM && lexem != platform.class_keyword -> {
                    tc.createEnum(lexem, security)
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
                            function_keyword -> isFunction = true
                            variable_keyword -> isVar = true
                            statement_start -> tc.openStatement()
                            statement_end -> tc.closeStatement()
                            block_start -> tc.startBlock()
                            block_end -> tc.endBlock()
                            break_keyword -> tc.markBreak()
                            continue_keyword -> tc.markContinue()
                            throw_operator -> tc.throwException()
                            assigment_operator -> tc.assignValue()
                            equals_operator -> tc.equals()
                            not_equals_operator -> tc.notEquals()
                            greater_operator -> tc.greaterThen()
                            less_operator -> tc.lessThen()
                            gore_operator -> tc.greaterOrEqualsThen()
                            lore_operator -> tc.lessOrEqualsThen()
                            multiply_operator -> tc.multiply()
                            divise_operator -> tc.divide()
                            modulo_operator -> tc.modulo()
                            power_operator -> tc.power()
                            breakpoint_keyword -> tc.markBreakpoint()
                            goto_keyword -> isGoto = true
                            return_keyword -> tc.addReturn()
                            typealias_keyword -> isTypeAlias = true
                            funcalias_keyword -> isFuncAlias = true
                            "\n" -> tc.incLine()
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

        //Body of lexerize
        checkPunctuation()
        while (pos < code.length) {
            when {
                hasString -> {
                    buffer.append(cur)
                }
                cur.isWhitespace() -> {
                    clearBuffer()
                    if (cur == '\n')
                        parseLexem("\n") //for correct printing errors and single line comments
                }
                cur.isJavaIdentifierPart() -> {
                    if (!prev.isJavaIdentifierPart()) clearBuffer()
                    buffer.append(cur)
                } //for digits, letters and _
                else -> {
                    if (!(prev.isJavaIdentifierPart() || prev.isWhitespace())) clearBuffer()
                    else {
                        val buf = buffer.toString() //TODO("Create parsing puctuation")
                        val canTokens = mutableListOf<String>()
                        for (token in punctation_keywords) {
                            if (token.startsWith(buf) && token.length > buf.length) {
                                canTokens.add(token)
                            }
                        }
                        if (canTokens.isEmpty()) clearBuffer()
                        else buffer.append(cur)
                    }
                } //punctuation
            }
            pos++
        }
        clearBuffer()
    }

    fun String.isPunctuation(): Boolean {
        for (c in this) {
            if (c.isJavaIdentifierPart() || c.isWhitespace()) return false
        }
        return true
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