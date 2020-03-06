/**
 *TODO("Create variable and function parsing")
 *TODO("Remove semicolons in typealias and funcalias")
 *TODO("Create construtor parsing")
 *TODO("Optimize")
 */

package com.snaulX.Tangara

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
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
            } catch (e: StringIndexOutOfBoundsException) {
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
        } catch (e: FileNotFoundException) {
            errors.add(
                ImportError(
                    line, "Platform by name $platformName not found. " +
                            "Platforms need saved in folder next to the compiler 'platforms/' " +
                            "and have extension .json"
                )
            )
        } catch (e: UnrecognizedPropertyException) {
            errors.add(
                ImportError(
                    line, "Invalid token ${e.propertyName} in platform $platformName."
                )
            )
        }
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
        var isConvert: Boolean = false
        var isIsOp: Boolean = false
        var lastLiteral: Boolean = false //last was literal or string or number
        var needEnd: Boolean = false //check for need of end of expression
        var security: SecurityDegree = SecurityDegree.PUBLIC
        var identifer: Identifer = Identifer.DEFAULT
        var typeName: String = ""

        /**
         * Push [lexem] to TokensCreator
         * @return Will can buffer clearing?
         */
        fun parseLexem(lexem: String): Boolean {
            val exprend = platform.expression_end //for optimizing
            when {
                singleComment -> {
                    if (lexem == "\n") singleComment = false
                }
                multiComment -> {
                    if (lexem == platform.multiline_comment_end) multiComment = false
                }
                needEnd -> {
                    needEnd = if (lexem != exprend) {
                        if (lexem == "\n") true
                        else if (exprend.isBlank() &&
                            (lexem == platform.single_comment_start ||
                             lexem == platform.multiline_comment_start)) {
                            needEnd = false
                            parseLexem(lexem)
                            false
                        }
                        else {
                            errors.add(
                                SyntaxError(
                                    line, "End of expression not found. Invalid end is $lexem"
                                )
                            )
                            false
                        }
                    } else false
                }
                isImport -> {
                    try {
                        import(Regex("(\\w+)").find(lexem)!!.destructured.component1())
                    } catch (e: KotlinNullPointerException) {
                        errors.add(InvalidNameError(line, "$lexem is not valid platform name"))
                    }
                    isImport = false
                    needEnd = true
                }
                isInclude -> {
                    val name = lexem.substring(lexem.length / 2)
                    if (name.endsWith(exprend)) {
                        tc.include(name.removeSuffix(exprend))
                        buffer.clear()
                        isInclude = false
                    } else {
                        try {
                            buffer.append(Regex("""(\w+|\.|\\|/)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(InvalidNameError(line, "$name is not valid including library name"))
                        }
                        return false
                    }
                }
                isUse -> {
                    val name = lexem.substring(lexem.length / 2)
                    if (name.endsWith(exprend)) {
                        tc.importPackage(name.removeSuffix(exprend))
                        buffer.clear()
                        isUse = false
                    } else {
                        try {
                            buffer.append(Regex("""(\w+|\.)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(InvalidNameError(line, "$name is not valid using package (namespace) name"))
                        }
                        return false
                    }
                }
                isLib -> {
                    val name = lexem.substring(lexem.length / 2)
                    if (name.endsWith(exprend)) {
                        tc.linkLibrary(name.removeSuffix(exprend))
                        buffer.clear()
                        isLib = false
                    } else {
                        try {
                            buffer.append(Regex("""(\w+|\.|\\|/)""").find(lexem)!!.destructured.component1())
                        } catch (e: KotlinNullPointerException) {
                            errors.add(InvalidNameError(line, "$name is not valid tokens library name"))
                        }
                        return false
                    }
                }
                isClass -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.createClass(lexem, security, identifer.classType)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of class"))
                    identifer = Identifer.DEFAULT
                    security = SecurityDegree.PUBLIC
                    isClass = false
                    needEnd = true
                }
                isFunction -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        val ft: FuncType? = identifer.funcType
                        if (ft == null) {
                            errors.add(SyntaxError(line, "Invalid type of function"))
                        } else {
                            tc.createMethod(lexem, typeName, security, ft)
                        }
                        identifer = Identifer.DEFAULT
                        security = SecurityDegree.PUBLIC
                        isFunction = false
                    }
                }
                isVar -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        if (lexem.isPunctuation() or (lexem == exprend)) {
                            when (identifer) {
                                Identifer.STATIC -> tc.createStaticField(typeName, "", security)
                                Identifer.FINAL -> tc.createFinalField(typeName, "", security)
                                Identifer.DEFAULT -> tc.createField(typeName, "", security)
                                else -> errors.add(SyntaxError(line, "Invalid type of variable"))
                            }
                        } else {
                            when (identifer) {
                                Identifer.STATIC -> tc.createStaticField(lexem, typeName, security)
                                Identifer.FINAL -> tc.createFinalField(lexem, typeName, security)
                                Identifer.DEFAULT -> tc.createField(lexem, typeName, security)
                                else -> errors.add(SyntaxError(line, "Invalid type of variable"))
                            }
                        }
                        identifer = Identifer.DEFAULT
                        security = SecurityDegree.PUBLIC
                        isVar = false
                    }
                }
                isTypeAlias -> {
                    tc.createTypeAlias(lexem.removeSuffix(exprend))
                    isTypeAlias = false
                }
                isFuncAlias -> {
                    tc.createFuncAlias(lexem.removeSuffix(exprend))
                    isFuncAlias = false
                }
                isGoto -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.goto(lexem)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of label for ${platform.goto_keyword} operator"))
                    isGoto = false
                    needEnd = true
                }
                isConvert -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.convertTo(lexem)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of type for convert operator"))
                    isConvert = false
                }
                isIsOp -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.addIs(lexem)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of type for ${platform.is_keyword} operator"))
                    isIsOp = false
                }
                identifer == Identifer.ENUM && lexem != platform.class_keyword -> {
                    tc.createEnum(lexem, security)
                    identifer = Identifer.DEFAULT
                    security = SecurityDegree.PUBLIC
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
                            string_char -> hasString = true
                            if_keyword -> tc.createIf()
                            else_keyword -> tc.createElse()
                            do_keyword -> tc.createDo()
                            case_keyword -> tc.createCase()
                            while_keyword -> tc.createWhile()
                            switch_keyword -> tc.createSwitch()
                            with_keyword -> tc.createWith()
                            in_operator -> tc.addIn()
                            convert_operator -> isConvert = true
                            is_keyword -> isIsOp = true
                            else -> if (lexem != expression_end) tc.callLiteral(lexem)
                        }
                    }
                }
            }
            return true
        }

        /**
         * Check on empty and pushing to lexemes value from buffer and clear it
         */
        fun clearBuffer() {
            if (buffer.isNotEmpty()) {
                val buf: String = buffer.toString()
                if (hasString) {
                    val index = buf.lastIndexOf(platform.string_char)
                    if (index >= 0) {
                        try {
                            if (buf[index - 1] != '\\') {
                                hasString = false
                                tc.loadValue(buffer.removeSuffix(platform.string_char))
                            }
                        }
                        catch (e: StringIndexOutOfBoundsException) {
                            hasString = false
                            tc.loadValue(buffer.removeSuffix(platform.string_char))
                        }
                        buffer.clear()
                    }
                } else {
                    if (parseLexem(buf)) buffer.clear()
                }
            }
        }

        //Body of lexerize
        checkPunctuation()
        while (pos < code.length) {
            when {
                hasString -> {
                    buffer.append(cur)
                    clearBuffer()
                }
                cur.isWhitespace() -> {
                    clearBuffer()
                    if (cur == '\n') {
                        tc.incLine()
                        line++
                        parseLexem("\n") //for correct printing errors and single line comments
                    }
                }
                cur.isJavaIdentifierPart() -> {
                    if (!prev.isJavaIdentifierPart()) clearBuffer()
                    buffer.append(cur)
                } //for digits, letters and _
                else -> {
                    if (prev.isJavaIdentifierPart() || prev.isWhitespace()) clearBuffer()
                    buffer.append(cur)
                    val buf = buffer.toString()
                    val canTokens = mutableListOf<String>()
                    for (token in punctation_keywords) {
                        if (token.startsWith(buf) && token.length > buf.length) {
                            canTokens.add(token)
                        }
                    }
                    if (canTokens.isEmpty()) clearBuffer()
                } //punctuation
            }
           pos++
        }
        val buf = buffer.toString()
        if (needEnd || (
            (buf != platform.expression_end || buf != platform.block_end
                    || !singleComment || !multiComment)
            && platform.expression_end.isNotBlank()))
            errors.add(SyntaxError(line, "Need end of expression"))
        else
            clearBuffer()
    }

    protected fun String.isPunctuation(): Boolean {
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