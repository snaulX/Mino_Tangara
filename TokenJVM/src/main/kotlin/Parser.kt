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
    val tokens: MutableList<Token> = mutableListOf()
    val buffer: StringBuilder = StringBuilder()
    private val tc: TokensCreator = TokensCreator()
    private val new_code: MutableList<String> = mutableListOf()
    var platform: Platform = Platform()
    private val current_line: String
        get() = new_code[0]

    //Regexes and examples
    val import_regex: Regex //import std;
            get() = Regex("""${platform.import_keyword}\s+(\w+)\s*${platform.expression_end}""")
    val use_regex: Regex //use System; use java.lang;
        get() = Regex("""${platform.use_keyword}\s+([\w.]+)\s*${platform.expression_end}""")
    val include_regex: Regex //include mscorlib; include MyLib.dll; include libs/SomeLib.dll; include lib\Lib.dll;
        get() = Regex("""${platform.include_keyword}\s+([\w.\\/]+)\s*${platform.expression_end}""")
    val lib_regex: Regex //lib standart; lib <libs/SomeLib>; lib <lib\Lib>; lib game/engine;
        get() = Regex("""${platform.lib_keyword}\s+(<?[\w\\/]+.?)\s*${platform.expression_end}""")
    val class_regex: Regex //public static class MyClass
        get() = Regex("""([${platform.public_keyword}\s+|${platform.private_keyword}\s+|${platform.protected_keyword}\s+]?)([${platform.final_keyword}\s+|${platform.data_keyword}\s+|${platform.static_keyword}\s+|
|${platform.abstract_keyword}\s+]?)${platform.class_keyword}\s+(\w+)""")
    val interface_regex: Regex //public interface MyInterface
        get() = Regex("""([${platform.public_keyword}\s+|${platform.private_keyword}\s+|${platform.protected_keyword}\s+]?)${platform.interface_keyword}\s+(\w+)""")


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
                when {
                    import_regex.matches(current_line) -> import(import_regex.find(current_line)!!.destructured.component1())
                    use_regex.matches(current_line) -> tc.importPackage(use_regex.find(current_line)!!.destructured.component1())
                    include_regex.matches(current_line) -> tc.include(include_regex.find(current_line)!!.destructured.component1())
                    lib_regex.matches(current_line) -> tc.linkLibrary(lib_regex.find(current_line)!!.destructured.component1())
                    class_regex.matches(current_line) -> {
                        val (security, type, name) = class_regex.find(current_line)!!.destructured
                        var sec = SecurityDegree.PUBLIC
                        var t = ClassType.DEFAULT
                        with(platform) {
                            when (security) {
                                "" -> sec = SecurityDegree.PUBLIC
                                public_keyword -> sec = SecurityDegree.PUBLIC
                                private_keyword -> sec = SecurityDegree.PRIVATE
                                protected_keyword -> sec = SecurityDegree.PROTECTED
                                else -> errors.add(SyntaxError(line, pos, "Syntax of security is invalid"))
                            }
                            when (type) {
                                "" -> t = ClassType.DEFAULT
                                data_keyword -> t = ClassType.DATA
                                final_keyword -> t = ClassType.SEALED
                                static_keyword -> t = ClassType.STATIC
                                abstract_keyword -> t = ClassType.ABSTRACT
                                else -> errors.add(SyntaxError(line, pos, "Syntax of class type is invalid"))
                            }
                        }
                        tc.createClass(name, sec, t)
                    }
                }
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                println(error)
            }
        }
    }

    fun setCode(setting_code: Collection<String>) {
        code = setting_code.toList()
        new_code.clear()
        new_code.addAll(code)
    }
}