package com.snaulX.Tangara

import kotlinx.cinterop.*
import platform.posix.*

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

    fun getCode(input: String, name: String) {
        appname = name
        code = input.split('\n')
    }

    fun parse(build: Boolean = false) {
        val file = fopen(appname, "wb")
        for (strline: String in code) {
            val newStrLine = strline.trimStart()
            if (newStrLine.startsWith(Platform.annotaion_start)) {
                //it`s annotation
            }
            else if (newStrLine.startsWith(Platform.directive_start)) {
                //it`s directive

            }
            else {
                val keyword = readKeyword()
                when (keyword) {
                    Platform.import_keyword -> import()
                }
            }
        }
    }

    fun readKeyword(): String {
        skipWhitespaces()
        val builder: StringBuilder = StringBuilder()
        while (current.isLetter()) {
            try {
                builder.append(current)
                pos++
            }
            catch (e: Exception) {
                break
            }
        }
        return builder.toString()
    }

    fun import() {
        //pass
    }
}