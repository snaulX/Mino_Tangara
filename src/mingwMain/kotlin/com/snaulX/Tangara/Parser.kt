package com.snaulX.Tangara

import platform.posix.fopen

class Parser {
    var import_keyword = "import"
    var use_keyword = "use"
    var lib_keyword = "lib"
    var class_keyword = "class"
    var public_keyword = "public"
    var private_keyword = "private"
    var protected_keyword = "protected"
    var sealed_keyword = "sealed"
    var function_keyword = "function"
    var variable_keyword = "var"
    var try_keyword = "try"
    var catch_keyword = "catch"
    var if_keyword = "if"
    var else_keyword = "else"
    var while_keyword = "while"
    var do_keyword = "do"
    var for_keyword = "for"
    var const_keyword = "const"
    var enum_keyword = "enum"
    var interface_keyword = "interface"
    var annotaion_start = "@"
    var annotaion_end = ""
    var array_start = "["
    var array_end = "]"
    var statement_start = "("
    var statement_end = ")"
    var block_start = "{"
    var block_end = "}"
    var directive_start = "#"
    var single_comment = "//"
    var multiline_comment_start = "/*"
    var multiline_comment_end = "*/"
    var typeof_keyword = "typeof"
    var is_keyword = "is"
    var with_keyword = "with"
    var switch_keyword = "switch"
    var case_keyword = "case"
    var default_keyword = "default"
    var break_keyword = "break"
    var continue_keyword = "continue"

    private var line = 0
    private var pos = 0
    private var code: List<String> = listOf()
    val errors: MutableList<TangaraError> = mutableListOf()
    private var buffer: StringBuilder = StringBuilder()
    private val current: Char
        get() {
            return code[line][pos]
        }
    var output: MutableList<Byte> = mutableListOf()

    fun skipWhitespaces(): Int {
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
                    return -1
                }
            }
        }
        return 0
    }

    fun getCode(input: String) {
        code = input.split('\n')
    }

    fun buildTokens(tokens_name: String) {
        val file = fopen(tokens_name, "wb")
    }
}