/**
 *TODO("Create parsing of all tokens with if without when")
 *TODO("Create char parsing")
 *TODO("Optimize")
 *TODO("Fix bugs")
 */

package com.snaulX.Tangara

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.snaulX.TokensAPI.*
import com.snaulX.TokensAPI.OperatorType.*
import com.snaulX.Tangara.Identifer.*
import com.snaulX.TokensAPI.SecurityDegree.*
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
    var targetPlatform: PlatformType = PlatformType.Common
    var header: HeaderType = HeaderType.Script

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
        var isFunction: Boolean = false
        var isGoto: Boolean = false
        var isTypeAlias: Boolean = false
        var isFuncAlias: Boolean = false
        var isConvert: Boolean = false
        var isInterface: Boolean = false
        var isStruct: Boolean = false
        var isIsOp: Boolean = false
        var isPackage: Boolean = false
        var needEnd: Boolean = false //check for need of end of expression
        var isNumber: Boolean = false //means that last lexem was number
        var isDouble: Boolean = false //means that have number dot or not
        var security: SecurityDegree = PUBLIC
        var identifer: Identifer = DEFAULT
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
                                    lexem == platform.multiline_comment_start)
                        ) {
                            needEnd = false
                            parseLexem(lexem)
                            false
                        } else {
                            errors.add(
                                SyntaxError(
                                    line, "End of expression not found. Invalid end is $lexem"
                                )
                            )
                            false
                        }
                    } else {
                        tc.insertExprEnd()
                        false
                    }
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
                        tc.importLibrary(name.removeSuffix(exprend))
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
                    if (Regex("\\w+").matches(lexem)) {
                        val ct: ClassType? = identifer.classType
                        if (ct == null)
                            errors.add(InvalidNameError(line, "Invalid type of class"))
                        else
                            tc.createClass(lexem, ct, security)
                    } else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of class"))
                    identifer = DEFAULT
                    security = PUBLIC
                    isClass = false
                }
                isFunction -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        if (Regex("\\w+").matches(lexem)) {
                            val ft: FuncType? = identifer.funcType
                            if (ft == null) {
                                errors.add(InvalidNameError(line, "Invalid type of function"))
                            } else {
                                //pass
                            }
                        }
                        identifer = DEFAULT
                        security = PUBLIC
                        isFunction = false
                    }
                }
                isTypeAlias -> {
                    val taName: String = lexem.removeSuffix(exprend)
                    if (Regex("\\w+").matches(taName))
                        tc.createClass(taName, ClassType.TYPEALIAS, security)
                    else
                        errors.add(InvalidNameError(line, "$taName is not valid name of typealias"))
                    isTypeAlias = false
                    identifer = DEFAULT
                    security = PUBLIC
                }
                isFuncAlias -> {
                    val faName: String = lexem.removeSuffix(exprend)
                    if (Regex("\\w+").matches(faName))
                        tc.createFunction(faName, "", FuncType.FUNCALIAS)
                    else
                        errors.add(InvalidNameError(line, "$faName is not valid name of funcalias"))
                    isFuncAlias = false
                    identifer = DEFAULT
                    security = PUBLIC
                }
                isGoto -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.goToLabel(lexem)
                    else
                        errors.add(
                            InvalidNameError(
                                line,
                                "$lexem is not valid name of label for ${platform.goto_keyword} operator"
                            )
                        )
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
                        tc.instanceOf(lexem)
                    else
                        errors.add(
                            InvalidNameError(
                                line,
                                "$lexem is not valid name of type for ${platform.is_keyword} operator"
                            )
                        )
                    isIsOp = false
                }
                isInterface -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.createClass(lexem, ClassType.INTERFACE, security)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of interface"))
                    identifer = DEFAULT
                    security = PUBLIC
                    isInterface = false
                }
                isStruct -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.createClass(lexem, ClassType.STRUCT, security)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of struct"))
                    identifer = DEFAULT
                    security = PUBLIC
                    isStruct = false
                }
                isPackage -> {
                    val packageName: String = lexem.removeSuffix(exprend)
                    if (Regex("\\w+").matches(packageName))
                        tc.setPackage(packageName)
                    else
                        errors.add(InvalidNameError(line, "$packageName is not valid name of package"))
                    isPackage = false
                }
                identifer == ENUM && lexem != platform.class_keyword -> {
                    if (Regex("\\w+").matches(lexem))
                        tc.createClass(lexem, ClassType.ENUM, security)
                    else
                        errors.add(InvalidNameError(line, "$lexem is not valid name of enum"))
                    identifer = DEFAULT
                    security = PUBLIC
                }
                else -> {
                    platform.run {
                        when (lexem) {
                            expression_end -> tc.insertExprEnd()
                            expression_separator -> tc.insertSeparator(isLiteral = false)
                            separator -> tc.insertSeparator(isLiteral = true)
                            import_keyword -> isImport = true
                            single_comment_start -> singleComment = true
                            multiline_comment_start -> multiComment = true
                            include_keyword -> isInclude = true
                            use_keyword -> isUse = true
                            lib_keyword -> isLib = true
                            public_keyword -> security = PUBLIC
                            private_keyword -> security = PRIVATE
                            protected_keyword -> security = PROTECTED
                            internal_keyword -> security = INTERNAL
                            final_keyword -> identifer = FINAL
                            data_keyword -> identifer = DATA
                            enum_keyword -> identifer = ENUM
                            static_keyword -> identifer = STATIC
                            abstract_keyword -> identifer = ABSTRACT
                            virtual_keyword -> identifer = VIRTUAL
                            override_keyword -> identifer = OVERRIDE
                            interface_keyword -> isInterface = true
                            class_keyword -> isClass = true
                            function_keyword -> isFunction = true
                            variable_keyword -> {
                                val vt: VarType? = identifer.varType
                                if (vt == null)
                                    errors.add(InvalidNameError(line, "Invalid type of variable"))
                                else
                                    tc.startVarDefinition(vt, security)
                                identifer = DEFAULT
                            }
                            statement_start -> tc.statement(start = true)
                            statement_end -> tc.statement(start = false)
                            block_start -> tc.block(start = true)
                            block_end -> tc.block(start = false)
                            break_keyword -> tc.insertLoopOperator(true)
                            continue_keyword -> tc.insertLoopOperator(false)
                            throw_operator -> tc.throwException()
                            assigment_operator -> tc.callOperator(ASSIGN)
                            equals_operator -> tc.callOperator(EQ)
                            not_equals_operator -> tc.callOperator(NOTEQ)
                            greater_operator -> tc.callOperator(GT)
                            less_operator -> tc.callOperator(LT)
                            gore_operator -> tc.callOperator(GORE)
                            lore_operator -> tc.callOperator(LORE)
                            add_operator -> tc.callOperator(ADD)
                            subtract_operator -> tc.callOperator(SUB)
                            multiply_operator -> tc.callOperator(MUL)
                            divise_operator -> tc.callOperator(DIV)
                            modulo_operator -> tc.callOperator(MOD)
                            addassign_operator -> tc.callOperator(ADDASSIGN)
                            subassign_operator -> tc.callOperator(SUBASSIGN)
                            mulassign_operator -> tc.callOperator(MULASSIGN)
                            divassign_operator -> tc.callOperator(DIVASSIGN)
                            modassign_operator -> tc.callOperator(MODASSIGN)
                            power_operator -> tc.callOperator(POW)
                            increment_operator -> tc.callOperator(INC)
                            decrement_operator -> tc.callOperator(DEC)
                            breakpoint_keyword -> tc.insertBreakpoint()
                            range_operator -> tc.callOperator(RANGE)
                            goto_keyword -> isGoto = true
                            return_keyword -> tc.insertReturn()
                            typealias_keyword -> isTypeAlias = true
                            funcalias_keyword -> isFuncAlias = true
                            string_char -> hasString = true
                            else_if_keyword -> {
                                if (else_if_keyword.isNotBlank()) {
                                    tc.insertElse()
                                    tc.insertIf()
                                }
                            }
                            if_keyword -> tc.insertIf()
                            else_keyword -> tc.insertElse()
                            do_keyword -> tc.insertLoop(LoopType.DO)
                            case_keyword -> tc.insertCase()
                            while_keyword -> tc.insertLoop(LoopType.WHILE)
                            switch_keyword -> tc.insertSwitch()
                            default_keyword -> {
                                tc.insertCase()
                                tc.callLiteral("_")
                            }
                            with_keyword -> tc.insertWith()
                            in_operator -> tc.callOperator(IN)
                            convert_operator -> isConvert = true
                            is_keyword -> isIsOp = true
                            yield_keyword -> tc.insertYield()
                            const_keyword -> {
                                tc.startVarDefinition(VarType.CONST, security)
                                security = PUBLIC
                                identifer = DEFAULT
                            }
                            interface_keyword -> isInterface = true
                            struct_keyword -> isStruct = true
                            implements_keyword -> tc.implements()
                            extends_keyword -> tc.extends()
                            actual_keyword -> tc.insertActual(actual = true)
                            expect_keyword -> tc.insertActual(actual = false)
                            try_keyword -> tc.insertTry()
                            catch_keyword -> tc.insertCatch()
                            finally_keyword -> tc.insertFinally()
                            nullable -> tc.insertNullable()
                            package_keyword -> isPackage = true
                            new_operator -> tc.insertNew()
                            for_keyword -> tc.insertLoop(LoopType.FOR)
                            foreach_keyword -> tc.insertLoop(LoopType.FOREACH)
                            directive_start -> tc.insertDirective()
                            after_case_operator -> tc.insertLambda(lambda = false)
                            else -> tc.callLiteral(lexem)
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
                                tc.callValue(buffer.removeSuffix(platform.string_char))
                            }
                        } catch (e: StringIndexOutOfBoundsException) {
                            hasString = false
                            tc.callValue(buffer.removeSuffix(platform.string_char))
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
                isNumber -> {
                    if (cur.isDigit()) {
                        buffer.append(cur)
                    } else if (cur == 'f') {
                        tc.callValue(buffer.toString().toFloat())
                        isNumber = false
                        isDouble = false
                    } else if (cur == 'd') {
                        tc.callValue(buffer.toString().toDouble())
                        isNumber = false
                        isDouble = false
                    } else if (cur == 'l') {
                        if (!isDouble) {
                            tc.callValue(buffer.toString().toLong())
                        } else {
                            errors.add(InvalidNumberError(line, "Number with dot cannot have type short"))
                        }
                        isNumber = false
                    } else if (cur == 's') {
                        if (!isDouble) {
                            tc.callValue(buffer.toString().toShort())
                        } else {
                            errors.add(InvalidNumberError(line, "Number with dot cannot have type short"))
                        }
                        isNumber = false
                    } else if (cur == 'b') {
                        if (!isDouble) {
                            tc.callValue(buffer.toString().toByte())
                        } else {
                            errors.add(InvalidNumberError(line, "Number with dot cannot have type byte"))
                        }
                        isNumber = false
                    } else if (cur == '.') {
                        if (!isDouble) {
                            isDouble = true
                        } else {
                            tc.callValue(buffer.toString().toDouble())
                            buffer.clear()
                            if (prev == '.') buffer.append('.')
                            isNumber = false
                            isDouble = false
                        }
                        buffer.append('.')
                    } else {
                        if (isDouble) {
                            tc.callValue(buffer.toString().toDouble())
                            isDouble = false
                        } else {
                            tc.callValue(buffer.toString().toInt())
                        }
                        isNumber = false
                    }
                    if (!isNumber) buffer.clear()
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
                    if (!prev.isJavaIdentifierPart()) {
                        clearBuffer()
                        if (cur.isDigit()) isNumber = true
                    }
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
                            && platform.expression_end.isNotBlank())
        )
            errors.add(SyntaxError(line, "Need end of expression"))
        else
            clearBuffer()
        tc.insertExprEnd()
    }

    protected fun String.isPunctuation(): Boolean {
        for (c in this) {
            if (c.isJavaIdentifierPart() || c.isWhitespace()) return false
        }
        return true
    }

    fun parse() {
        tc.setOutput(appname)
        tc.setTargetPlatform(targetPlatform)
        tc.setHeader(header)
        lexerize()
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }
}