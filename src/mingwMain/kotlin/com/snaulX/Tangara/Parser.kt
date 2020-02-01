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
                    createError(SyntaxError(line, pos, "Directive haven`t expression"))
                }
            }
            else if (keyword.startsWith(Platform.annotaion_start)) {
                //it`s annotation
            }
            else if (keyword.startsWith(Platform.statement_start)) {
                //it`s statement
            }
            else {
                when (keyword) {
                    Platform.import_keyword -> import()
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

    fun readInteger(): String {
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
        return buffer.toString()
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
        //pass
        println("import")
    }

    fun createError(error: TangaraError) = errors.add(error)

    fun pushLiteral() {
        //check variable
    }
}