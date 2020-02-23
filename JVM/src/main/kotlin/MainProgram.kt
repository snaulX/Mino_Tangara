package com.snaulX.Tangara

import java.io.FileReader

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        /*println("""
            Tangara 2020-2020
            Author: snaulX
            All copyrights reserved.
            GitHub repository: https://github.com/snaulX/Tangara
        """.trimIndent())*/
        //for test in IDE
        val parser: Parser = Parser()
        val code: List<String> = listOf("public final class MyClass")
        parser.setCode(code)
        parser.parse()
    }
    else {
        val parser: Parser = Parser()
        val file: FileReader = FileReader(args[0])
        parser.setCode(file.readLines())
        parser.parse()
    }
}