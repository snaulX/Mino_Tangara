package com.snaulX.Tangara

import java.io.File

fun main(args: Array<String>) {
    val start = System.currentTimeMillis()
    if (args.isEmpty()) {
        println("""
            Tangara 2020-2020
            Author: snaulX
            All copyrights reserved.
            GitHub repository: https://github.com/snaulX/Tangara
        """.trimIndent())
        //next code for test in IDE
        val parser: Parser = Parser()
        val file: File = File("TestMinoApp.mino")
        parser.appname = file.nameWithoutExtension
        parser.code = file.readText()
        parser.parse()
    }
    else {
        val parser: Parser = Parser()
        val file: File = File(args[0])
        parser.appname = file.nameWithoutExtension
        parser.code = file.readText()
        parser.parse()
    }
    println("Compilation time: ${System.currentTimeMillis() - start} ms")
}