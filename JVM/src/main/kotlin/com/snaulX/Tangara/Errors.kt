package com.snaulX.Tangara

fun TangaraError.getError() = "${this::class.simpleName} in line $line position $position. $message\n"

class ImportError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class IncludeError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class LibError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class SyntaxError(line: Int, position: Int, message: String) : TangaraError(line, position, message)