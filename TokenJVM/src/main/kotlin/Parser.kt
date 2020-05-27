/**
 *TODO("Optimize")
 */

package com.snaulX.Tangara

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.snaulX.TokensAPI.*
import com.snaulX.TokensAPI.OperatorType.*
import com.snaulX.Tangara.Identifer.*
import com.snaulX.TokensAPI.SecurityDegree.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.io.FileNotFoundException
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
        get() = try {
                code[pos - 1]
            } catch (e: StringIndexOutOfBoundsException) {
                ' '
            }
    private val punctationKeywords: MutableList<String> = mutableListOf()
    private val repeatKeywords: MutableList<String> = mutableListOf()
    private val keywords: MutableList<String> = mutableListOf()
    var targetPlatform: PlatformType = PlatformType.Common
    var header: HeaderType = HeaderType.Script

    fun checkPunctuation() {
        punctationKeywords.clear()
        repeatKeywords.clear()
        keywords.clear()
        platform::class.memberProperties.forEach {
            val keyword = it.call(platform).toString()
            if (keywords.contains(keyword) && !repeatKeywords.contains(keyword))
                repeatKeywords.add(keyword)
            else
                keywords.add(keyword)
            if (keyword.isPunctuation())
                punctationKeywords.add(keyword)
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
        var hasChar: Boolean = false
        var isLib: Boolean = false
        var isImport: Boolean = false
        var isUse: Boolean = false
        var isInclude: Boolean = false
        var isClass: Boolean = false
        var isFunction: Boolean = false
        var isGoto: Boolean = false
        var isTypeAlias: Boolean = false
        var isFuncAlias: Boolean = false
        var isInterface: Boolean = false
        var isStruct: Boolean = false
        var isIsOp: Boolean = false
        var isPackage: Boolean = false
        var isOperator: Boolean = false
        var isDirective: Boolean = false
        var isCtor: Boolean = false
        var needEnd: Boolean = false //check for need of end of expression
        var isNumber: Boolean = false //means that last lexem was number
        var isDouble: Boolean = false //means that have number dot or not
        var security: SecurityDegree = PUBLIC
        var identifer: Identifer = DEFAULT
        var typeName: String = ""


        fun checkLexem(lexem: String) {
            val repeatLexem: Boolean = repeatKeywords.contains(lexem)
            if (lexem == "\n") tc.incLine()
            platform.run {
                if (lexem == expression_end) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertExprEnd()
                    if (!repeatLexem)
                        return
                }
                if (lexem == expression_separator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertSeparator(isLiteral = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == separator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertSeparator(isLiteral = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == import_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isImport = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == single_comment_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    singleComment = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == multiline_comment_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    multiComment = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == include_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isInclude = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == use_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isUse = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == lib_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isLib = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == public_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    security = PUBLIC
                    if (!repeatLexem)
                        return
                }
                if (lexem == private_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    security = PRIVATE
                    if (!repeatLexem)
                        return
                }
                if (lexem == protected_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    security = PROTECTED
                    if (!repeatLexem)
                        return
                }
                if (lexem == final_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = FINAL
                    if (!repeatLexem)
                        return
                }
                if (lexem == data_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = DATA
                    if (!repeatLexem)
                        return
                }
                if (lexem == enum_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = ENUM
                    if (!repeatLexem)
                        return
                }
                if (lexem == static_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = STATIC
                    if (!repeatLexem)
                        return
                }
                if (lexem == abstract_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = ABSTRACT
                    if (!repeatLexem)
                        return
                }
                if (lexem == virtual_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = VIRTUAL
                    if (!repeatLexem)
                        return
                }
                if (lexem == override_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    identifer = OVERRIDE
                    if (!repeatLexem)
                        return
                }
                if (lexem == interface_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isInterface = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == class_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isClass = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == function_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isFunction = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == variable_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    val vt: VarType? = identifer.varType
                    if (vt == null)
                        errors.add(InvalidNameError(line, "Invalid type of variable"))
                    else
                        tc.startVarDefinition(vt, security)
                    identifer = DEFAULT
                    security = PUBLIC
                    if (!repeatLexem)
                        return
                }
                if (lexem == statement_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.statement(start = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == statement_end) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.statement(start = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == block_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.block(start = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == block_end) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.block(start = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == array_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.sequence(start = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == array_end) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.sequence(start = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == break_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoopOperator(_break = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == continue_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoopOperator(_break = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == throw_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.throwException()
                    if (!repeatLexem)
                        return
                }
                if (lexem == assigment_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(ASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == equals_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(EQ)
                    if (!repeatLexem)
                        return
                }
                if (lexem == not_equals_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(NOTEQ)
                    if (!repeatLexem)
                        return
                }
                if (lexem == greater_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(GT)
                    if (!repeatLexem)
                        return
                }
                if (lexem == less_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(LT)
                    if (!repeatLexem)
                        return
                }
                if (lexem == gore_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(GORE)
                    if (!repeatLexem)
                        return
                }
                if (lexem == lore_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(LORE)
                    if (!repeatLexem)
                        return
                }
                if (lexem == add_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(ADD)
                    if (!repeatLexem)
                        return
                }
                if (lexem == subtract_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(SUB)
                    if (!repeatLexem)
                        return
                }
                if (lexem == multiply_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(MUL)
                    if (!repeatLexem)
                        return
                }
                if (lexem == divise_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(DIV)
                    if (!repeatLexem)
                        return
                }
                if (lexem == modulo_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(MOD)
                    if (!repeatLexem)
                        return
                }
                if (lexem == addassign_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(ADDASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == subassign_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(SUBASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == mulassign_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(MULASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == divassign_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(DIVASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == modassign_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(MODASSIGN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == power_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(POW)
                    if (!repeatLexem)
                        return
                }
                if (lexem == increment_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(INC)
                    if (!repeatLexem)
                        return
                }
                if (lexem == decrement_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(DEC)
                    if (!repeatLexem)
                        return
                }
                if (lexem == breakpoint_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertBreakpoint()
                    if (!repeatLexem)
                        return
                }
                if (lexem == range_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(RANGE)
                    if (!repeatLexem)
                        return
                }
                if (lexem == goto_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isGoto = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == return_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertReturn(short = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == short_return_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertReturn(short = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == typealias_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isTypeAlias = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == funcalias_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isFuncAlias = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == string_char) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    hasString = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == char_char) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    hasChar = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == else_if_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertElse()
                    tc.insertIf()
                    if (!repeatLexem)
                        return
                }
                if (lexem == if_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertIf()
                    if (!repeatLexem)
                        return
                }
                if (lexem == else_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertElse()
                    if (!repeatLexem)
                        return
                }
                if (lexem == do_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoop(LoopType.DO)
                    if (!repeatLexem)
                        return
                }
                if (lexem == case_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertCase()
                    if (!repeatLexem)
                        return
                }
                if (lexem == while_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoop(LoopType.WHILE)
                    if (!repeatLexem)
                        return
                }
                if (lexem == switch_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertSwitch()
                    if (!repeatLexem)
                        return
                }
                if (lexem == default_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertCase()
                    tc.callLiteral("_")
                    if (!repeatLexem)
                        return
                }
                if (lexem == with_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertWith()
                    if (!repeatLexem)
                        return
                }
                if (lexem == in_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(IN)
                    if (!repeatLexem)
                        return
                }
                if (lexem == convert_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callOperator(CONVERTTO)
                    if (!repeatLexem)
                        return
                }
                if (lexem == is_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isIsOp = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == yield_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertYield()
                    if (!repeatLexem)
                        return
                }
                if (lexem == const_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.startVarDefinition(VarType.CONST, security)
                    security = PUBLIC
                    identifer = DEFAULT
                    if (!repeatLexem)
                        return
                }
                if (lexem == interface_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isInterface = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == struct_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isStruct = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == implements_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.implements()
                    if (!repeatLexem)
                        return
                }
                if (lexem == extends_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.extends()
                    if (!repeatLexem)
                        return
                }
                if (lexem == actual_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertActual(actual = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == expect_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertActual(actual = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == try_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertTry()
                    if (!repeatLexem)
                        return
                }
                if (lexem == catch_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertCatch()
                    if (!repeatLexem)
                        return
                }
                if (lexem == finally_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertFinally()
                    if (!repeatLexem)
                        return
                }
                if (lexem == nullable) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertNullable()
                    if (!repeatLexem)
                        return
                }
                if (lexem == package_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isPackage = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == new_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertNew()
                    if (!repeatLexem)
                        return
                }
                if (lexem == for_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoop(LoopType.FOR)
                    if (!repeatLexem)
                        return
                }
                if (lexem == foreach_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLoop(LoopType.FOREACH)
                    if (!repeatLexem)
                        return
                }
                if (lexem == directive_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertDirective()
                    isDirective = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == after_case_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLambda(lambda = false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == lambda_operator) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertLambda(lambda = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == true_value) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callValue(true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == false_value) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callValue(false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == null_value) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.callValue(null)
                    if (!repeatLexem)
                        return
                }
                if (lexem == operator_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isOperator = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == readonly_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.startVarDefinition(VarType.FINAL, security)
                    security = PUBLIC
                    if (!repeatLexem)
                        return
                }
                if (lexem == constructor_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    isCtor = true
                    if (!repeatLexem)
                        return
                }
                if (lexem == async_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertAsync(false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == await_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertAsync(true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == out_parameter) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.parameterType(true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == in_parameter) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.parameterType(false)
                    if (!repeatLexem)
                        return
                }
                if (lexem == ref_keyword) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertRef()
                    if (!repeatLexem)
                        return
                }
                if (lexem == generic_start) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertGeneric(start = true)
                    if (!repeatLexem)
                        return
                }
                if (lexem == generic_end) {
                    if (repeatLexem) {
                        tc.insertDirective()
                        tc.callLiteral("try")
                    }
                    tc.insertGeneric(start = false)
                    if (!repeatLexem)
                        return
                }
            }
            if (repeatLexem) {
                tc.insertDirective()
                tc.callLiteral("endtry")
            }
        }

        /**
         * Push [lexem] to TokensCreator
         * @return Will must buffer clearing?
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
                    import(lexem)
                    isImport = false
                    needEnd = true
                }
                isInclude -> {
                    if (lexem.endsWith(exprend)) {
                        tc.include(lexem.removeSuffix(exprend))
                        tc.insertExprEnd()
                        isInclude = false
                    } else {
                        return false
                    }
                }
                isUse -> {
                    if (lexem.endsWith(exprend)) {
                        tc.importPackage(lexem.removeSuffix(exprend))
                        tc.insertExprEnd()
                        isUse = false
                    } else {
                        return false
                    }
                }
                isLib -> {
                    if (lexem.endsWith(exprend)) {
                        tc.importLibrary(lexem.removeSuffix(exprend))
                        tc.insertExprEnd()
                        isLib = false
                    } else {
                        return false
                    }
                }
                isClass -> {
                        val ct: ClassType? = identifer.classType
                        if (ct == null)
                            errors.add(InvalidNameError(line, "Invalid type of class"))
                        else
                            tc.createClass(lexem, ct, security)
                    identifer = DEFAULT
                    security = PUBLIC
                    isClass = false
                }
                isFunction -> {
                    if (typeName.isEmpty()) typeName = lexem
                    else {
                        val ft: FuncType? = identifer.funcType
                            if (ft == null) {
                                errors.add(InvalidNameError(line, "Invalid type of function"))
                            } else {
                                tc.createFunction(lexem, typeName, ft, security)
                            }
                        identifer = DEFAULT
                        security = PUBLIC
                        isFunction = false
                        typeName = ""
                    }
                }
                isTypeAlias -> {
                    val taName: String = lexem
                    tc.createClass(taName, ClassType.TYPEALIAS, security)
                    isTypeAlias = false
                    identifer = DEFAULT
                    security = PUBLIC
                }
                isFuncAlias -> {
                    val faName: String = lexem
                    tc.createFunction(faName, "", FuncType.FUNCALIAS, security)
                    isFuncAlias = false
                    identifer = DEFAULT
                    security = PUBLIC
                }
                isGoto -> {
                    tc.goToLabel(lexem)
                    isGoto = false
                    needEnd = true
                }
                isIsOp -> {
                    tc.instanceOf(lexem)
                    isIsOp = false
                }
                isInterface -> {
                    tc.createClass(lexem, ClassType.INTERFACE, security)
                    identifer = DEFAULT
                    security = PUBLIC
                    isInterface = false
                }
                isStruct -> {
                    tc.createClass(lexem, ClassType.STRUCT, security)
                    identifer = DEFAULT
                    security = PUBLIC
                    isStruct = false
                }
                isPackage -> {
                    if (lexem.endsWith(exprend)) {
                        tc.setPackage(lexem.removeSuffix(exprend))
                        tc.insertExprEnd()
                        isPackage = false
                    } else {
                        return false
                    }
                }
                isOperator -> {
                    tc.createFunction("", lexem, FuncType.OPERATOR, security)
                    isOperator = false
                    security = PUBLIC
                }
                isDirective -> {
                    if (lexem != "\n") tc.callLiteral(lexem)
                    else isDirective = false
                }
                isCtor -> {
                    var name: String = ""
                    if (lexem != platform.statement_start && lexem != platform.block_start)
                        name = lexem
                    tc.createFunction(name, "", FuncType.CONSTRUCTOR, security)
                    security = PUBLIC
                    isCtor = false
                }
                identifer == ENUM && lexem != platform.class_keyword -> {
                    tc.createClass(lexem, ClassType.ENUM, security)
                    identifer = DEFAULT
                    security = PUBLIC
                }
                else -> {
                    if (!keywords.contains(lexem) && lexem != "\n") {
                        tc.callLiteral(lexem)
                    } else {
                        checkLexem(lexem)
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
                } else if (hasChar) {
                    val index = buf.lastIndexOf(platform.char_char)
                    if (index >= 0) {
                        try {
                            if (buf[index - 1] != '\\') {
                                hasChar = false
                                tc.callValue(buffer.removeSuffix(platform.char_char))
                            }
                        } catch (e: StringIndexOutOfBoundsException) {
                            hasChar = false
                            tc.callValue(buffer.removeSuffix(platform.char_char).single())
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
                hasString || hasChar -> {
                    buffer.append(cur)
                    clearBuffer()
                }
                isNumber -> {
                    when {
                        cur.isDigit() -> {
                            buffer.append(cur)
                        }
                        cur == 'f' -> {
                            tc.callValue(buffer.toString().toFloat())
                            isNumber = false
                            isDouble = false
                        }
                        cur == 'd' -> {
                            tc.callValue(buffer.toString().toDouble())
                            isNumber = false
                            isDouble = false
                        }
                        cur == 'l' -> {
                            if (!isDouble) {
                                tc.callValue(buffer.toString().toLong())
                            } else {
                                errors.add(InvalidNumberError(line, "Number with dot cannot have type short"))
                            }
                            isNumber = false
                        }
                        cur == 's' -> {
                            if (!isDouble) {
                                tc.callValue(buffer.toString().toShort())
                            } else {
                                errors.add(InvalidNumberError(line, "Number with dot cannot have type short"))
                            }
                            isNumber = false
                        }
                        cur == 'b' -> {
                            if (!isDouble) {
                                tc.callValue(buffer.toString().toByte())
                            } else {
                                errors.add(InvalidNumberError(line, "Number with dot cannot have type byte"))
                            }
                            isNumber = false
                        }
                        cur == '.' -> {
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
                        }
                        else -> {
                            if (isDouble) {
                                tc.callValue(buffer.toString().toDouble())
                                isDouble = false
                            } else {
                                tc.callValue(buffer.toString().toInt())
                            }
                            isNumber = false
                            buffer.append(cur)
                            parseLexem(cur.toString())
                        }
                    }
                    if (!isNumber) {
                        buffer.clear()
                    }
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
                    for (token in punctationKeywords) {
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
        if (needEnd && (
                    (buf != platform.expression_end || buf != platform.block_end
                            || !singleComment || !multiComment)
                            && platform.expression_end.isNotBlank())
        )
            errors.add(SyntaxError(line, "Need end of expression"))
        else
            clearBuffer()
        tc.insertExprEnd()
    }

    private fun String.isPunctuation(): Boolean {
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
        } else {
            println("PARSING SUCCESFUL")
        }
    }
}