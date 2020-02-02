package com.snaulX.Tangara

class Parser {
    var appname = ""
    private var line = 0
    private var pos = 0
    private var code: List<String> = listOf()
    val errors: MutableList<TangaraError> = mutableListOf()
    val buffer: StringBuilder = StringBuilder()
    private val current: Char
        get() {
            return code[line][pos]
        }

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

    fun getCode(input: String, name: String) {
        appname = name
        code = input.split('\n')
    }

    fun parse(build: Boolean = false) {
        var multiline_comment: Boolean = false
        //val tc: TokensCreator = TokensCreator()
        //if (build) tc.setOutput("$appname.tokens")
        for (strline: String in code) {
            if (!skipWhitespaces()) break
            val keyword = readKeyword()
            if (keyword.isEmpty()) {
                if (current.isDigit()) {
                    readInteger()
                }
                else {
                    break
                }
            }
            if (keyword.startsWith(Platform.directive_start)) {
                //it`s directive
                if (keyword == Platform.directive_start) {
                    createError(SyntaxError(line, pos, "Directive haven`t name"))
                }
            }
            else if (keyword.startsWith(Platform.annotaion_start)) {
                //it`s annotation
            }
            else if (keyword.startsWith(Platform.statement_start)) {
                //it`s statement
            }
            else if (keyword.startsWith(Platform.array_start)) {
                //it`s array
            }
            else if (keyword.startsWith(Platform.string_char)) {
                //it`s string
            }
            else if (keyword.startsWith(Platform.char_char)) {
                //it`s char
            }
            else if (keyword.startsWith(Platform.single_comment)) {
                continue
            }
            else if (keyword.startsWith(Platform.multiline_comment_start)) {
                multiline_comment = true
            }
            else if (keyword.contains(Platform.multiline_comment_end) && multiline_comment) {
                multiline_comment = false
            }
            else {
                when (keyword) {
                    Platform.import_keyword -> import()
                    Platform.lib_keyword -> lib()
                    Platform.use_keyword -> use()
                    else -> pushLiteral()
                }
            }
        }
        if (errors.isNotEmpty()) {
            for (error: TangaraError in errors) {
                error(error.getError())
            }
        }
    }

    fun use() {
        val using_package: String = readKeyword()
        println("${Platform.use_keyword} $using_package")
    }

    fun readInteger(): Int {
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
        if (skipWhitespaces()) {
            while (current.isDigit() || current == '.') {
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
        if (skipWhitespaces()) {
            while (current.isLetter()) {
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

    fun import() {
        val platform = readKeyword()
        println("${Platform.import_keyword} $platform")
    }

    fun lib() {
        val lib_name = readKeyword()
        println("${Platform.lib_keyword} $lib_name")
    }

    fun createError(error: TangaraError) = errors.add(error)

    fun pushLiteral() {
        //check variable
    }
}