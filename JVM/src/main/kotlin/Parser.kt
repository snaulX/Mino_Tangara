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
    val buffer: StringBuilder = StringBuilder()
    private val tc: TokensCreator = TokensCreator()
    private val new_code: MutableList<String> = mutableListOf()
    var platform: Platform = Platform()
    private var current_line: String
        set(v) { new_code[0] = v }
        get() = new_code[0]

    //Regular expressions for parsing and examples for it
    val security_regex: Regex
        get() = Regex("""${platform.public_keyword}\s+|${platform.private_keyword}\s+|${platform.protected_keyword}\s+""")
    val classType_regex: Regex
        get() = Regex("""${platform.final_keyword}\s+|${platform.data_keyword}\s+|${platform.static_keyword}
\s+|${platform.abstract_keyword}\s+|${platform.enum_keyword}\s+""")
    val import_regex: Regex //import std;
        get() = Regex("""${platform.import_keyword}\s+(\w+)\s*${platform.expression_end}""")
    val use_regex: Regex //use System; use java.lang;
        get() = Regex("""${platform.use_keyword}\s+([\w.]+)\s*${platform.expression_end}""")
    val include_regex: Regex //include mscorlib; include MyLib.dll; include libs/SomeLib.dll; include lib\Lib.dll;
        get() = Regex("""${platform.include_keyword}\s+([\w.\\/]+)\s*${platform.expression_end}""")
    val lib_regex: Regex //lib standart; lib <libs/SomeLib>; lib <lib\Lib>; lib game/engine;
        get() = Regex("""${platform.lib_keyword}\s+(<?[\w\\/]+>?)\s*${platform.expression_end}""")
    val class_regex: Regex //public static class MyClass
        get() = Regex("""($security_regex)?($classType_regex)?${platform.class_keyword}\s+(\w+)""")
    val interface_regex: Regex //public interface MyInterface
        get() = Regex("""($security_regex)?${platform.interface_keyword}\s+(\w+)""")
    val enum_regex: Regex //public enum MyEnum
        get() = Regex("""($security_regex)?${platform.enum_keyword}\s+(\w+)""")
    val var_regex: Regex //public final var myVar; private var string str = "Hello World";
        get() = Regex("""($security_regex)?\s+(${platform.static_keyword}|${platform.final_keyword})?\s+
${platform.variable_keyword}\s+(\w*)\s+(\w+)""")


    fun setCode(setting_code: Collection<String>) {
        code = setting_code.toList()
        new_code.clear()
        new_code.addAll(code)
    }

    fun skipWhitespaces(): Boolean {
        current_line.trimStart()
        if (current_line.isEmpty()) {
            line++
            new_code.removeAt(0)
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
        var hasOpenBracket = false
        var hasCloseBracket = true
        while (skipWhitespaces()) {
            current_line = current_line.trimEnd()
            when {
                import_regex.matches(current_line) -> import(import_regex.find(current_line)!!.destructured.component1())
                use_regex.matches(current_line) -> tc.importPackage(use_regex.find(current_line)!!.destructured.component1())
                include_regex.matches(current_line) -> tc.include(include_regex.find(current_line)!!.destructured.component1())
                lib_regex.matches(current_line) -> tc.linkLibrary(lib_regex.find(current_line)!!.destructured.component1())
                class_regex.matches(current_line) -> {
                    val (security, type, name) = class_regex.find(current_line)!!.destructured
                    tc.createClass(name, checkSecurity(security) ?: SecurityDegree.PUBLIC, checkClassType(type) ?: ClassType.DEFAULT)
                }
                interface_regex.matches(current_line) -> {
                    val (security, name) = interface_regex.find(current_line)!!.destructured
                    tc.createInterface(name, checkSecurity(security) ?: SecurityDegree.PUBLIC)
                }
                enum_regex.matches(current_line) -> {
                    val (security, name) = enum_regex.find(current_line)!!.destructured
                    tc.createEnum(name, checkSecurity(security) ?: SecurityDegree.PUBLIC)
                }
                var_regex.matches(current_line) -> {
                    val (security, var_type, type, name) = var_regex.find(current_line)!!.destructured
                    when (var_type) {
                        platform.static_keyword -> tc.createStaticField(name, type, checkSecurity(security) ?: SecurityDegree.PUBLIC)
                        platform.final_keyword -> tc.createFinalField(name, type, checkSecurity(security) ?: SecurityDegree.PUBLIC)
                        else -> tc.createField(name, type, checkSecurity(security) ?: SecurityDegree.PUBLIC)
                    }
                }
            }
        }
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
                errors.add(SyntaxError(line, pos, "Syntax of security is invalid"))
                return null
            }
        }
    }

    fun checkClassType(t: String): ClassType? {
        return when (t) {
            "" -> ClassType.DEFAULT
            platform.data_keyword -> ClassType.DATA
            platform.final_keyword -> ClassType.SEALED
            platform.static_keyword -> ClassType.STATIC
            platform.abstract_keyword -> ClassType.ABSTRACT
            platform.enum_keyword -> ClassType.ENUM
            else -> {
                errors.add(SyntaxError(line, pos, "Syntax of class type is invalid"))
                return null
            }
        }
    }
}