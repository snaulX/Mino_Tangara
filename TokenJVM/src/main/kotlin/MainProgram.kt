package com.snaulX.Tangara

import com.snaulX.TokensAPI.*
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
            Tangara 2020-2020
            Author: snaulX
            All copyrights reserved.
            GitHub repository: https://github.com/mino-lang/Tangara
            For get commands write -h or --help
        """.trimIndent())
    }
    else {
        if (args.size == 1 && (args[0] == "-h" || args[0] == "--help")) {
            println("""
            |Syntax: java -jar Tangara.jar [name of compiling file] [commands]
            |Commands of Tangara:
            |   -platform [name of platform for import]       Import platform in the start of compilation
            |   -o [name of output file without extension]    Name of compiling tokens file without extension
            |   -type [name of type of compiling file]        Types:
            |        ${HeaderType.values().joinToString("\n\t\t")}
            |   -target [name of target platform]             Platforms:
            |        ${PlatformType.values().joinToString("\n\t\t")}
        """.trimMargin())
        }
        else {
            val start = System.currentTimeMillis()
            val parser: Parser = Parser()
            val file: File = File(args[0])
            var arg_index: Int = args.indexOf("-platform")
            if (arg_index > 0) {
                try {
                    parser import args[arg_index + 1]
                } catch (e: IndexOutOfBoundsException) {
                    throw InvalidCommandLineArgumentException("Name of platform not found")
                }
            }
            arg_index = args.indexOf("-o")
            if (arg_index > 0) {
                try {
                    parser.appname = args[arg_index + 1]
                } catch (e: IndexOutOfBoundsException) {
                    throw InvalidCommandLineArgumentException("Name of output file not found")
                }
            } else {
                parser.appname = file.nameWithoutExtension
            }
            arg_index = args.indexOf("-target")
            if (arg_index > 0) {
                var targetName = ""
                try {
                    targetName = args[++arg_index]
                    parser.targetPlatform = PlatformType.valueOf(targetName)
                    if (parser.targetPlatform == PlatformType.Other) {
                        //while nothing
                    }
                } catch (e: IndexOutOfBoundsException) {
                    throw InvalidCommandLineArgumentException(
                        "Name of target platform not found"
                    )
                } catch (e: IllegalArgumentException) {
                    throw InvalidCommandLineArgumentException(
                        "Name of target platform '$targetName' is invalid"
                    )
                }
            } else {
                parser.targetPlatform = PlatformType.Common
            }
            arg_index = args.indexOf("-type")
            if (arg_index > 0) {
                var typeName = ""
                try {
                    typeName = args[arg_index + 1]
                    parser.header = HeaderType.valueOf(typeName)
                } catch (e: IndexOutOfBoundsException) {
                    throw InvalidCommandLineArgumentException(
                        "Name of type of compiling program not found"
                    )
                } catch (e: IllegalArgumentException) {
                    throw InvalidCommandLineArgumentException(
                        "Name of type of compiling program '$typeName' is invalid"
                    )
                }
            } else {
                parser.header = HeaderType.Script
            }
            parser.code = file.readText()
            parser.parse()
            println("Compilation time: ${System.currentTimeMillis() - start} ms")
            if (parser.errors.isNotEmpty()) exitProcess(-1)
        }
    }
}