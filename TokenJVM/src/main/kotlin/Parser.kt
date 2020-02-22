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
            get() = Regex("""${platform.import_keyword}(\s+)(\w+)(\s*)${platform.expression_end}""")
    val use_regex: Regex //use System; use java.lang;
        get() = Regex("""${platform.use_keyword}(\s+)([\w.]+)(\s*)${platform.expression_end}""")
    val include_regex: Regex //include mscorlib; include MyLib.dll; include libs/SomeLib.dll; include lib\Lib.dll;
        get() = Regex("""${platform.include_keyword}(\s+)([\w.\\/]+)(\s*)${platform.expression_end}""")
    val lib_regex: Regex //lib standart; lib <libs/SomeLib>; lib <lib\Lib>; lib game/engine;
        get() = Regex("""${platform.lib_keyword}(\s+)(<?[\w\\/]+.?)(\s*)${platform.expression_end}""")


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
        while (skipWhitespaces()) {
                when {
                    import_regex.matches(current_line) -> import(import_regex.find(current_line)!!.destructured.component2())
                    use_regex.matches(current_line) -> tc.importPackage(use_regex.find(current_line)!!.destructured.component2())
                    include_regex.matches(current_line) -> tc.include(include_regex.find(current_line)!!.destructured.component2())
                    lib_regex.matches(current_line) -> tc.linkLibrary(lib_regex.find(current_line)!!.destructured.component2())
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