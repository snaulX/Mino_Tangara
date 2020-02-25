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
        parser.appname = "TestMinoApp"
        parser.code = """
            import std; //importing platform std
            lib standart; //link tokens library standart
            use System; //using namespace System
            include mscorlib; //including base library of .NET
            Console.WriteLine("Hello World"); //printing Hello World on the console
            /*
            Multiline comment
            is
            work
            */
        """.trimIndent()
        parser.parse()
    }
    else {
        val parser: Parser = Parser()
        val file: FileReader = FileReader(args[0])
        parser.code = file.readText()
        parser.parse()
    }
}