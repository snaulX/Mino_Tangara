package com.snaulX.Tangara

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.pointed
import platform.posix.*

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
            Tangara 2020-2020
            Author: snaulX
            All copyrights reserved.
            GitHub repository: https://github.com/snaulX/Tangara
        """.trimIndent())
    }
    else {
        val parser: Parser = Parser()
        val file: CPointer<FILE>? = fopen(args[0], "r")
        var c: Int = getc(file)
        val buffer: StringBuilder = StringBuilder()
        while (c != EOF)
        {
            buffer.append(c.toChar())
        }
        parser.getCode(buffer.toString())
    }
}