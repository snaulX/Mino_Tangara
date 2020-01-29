package com.snaulX.Tangara

class ImportError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class UseError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class LibError(line: Int, position: Int, message: String) : TangaraError(line, position, message)

class SyntaxError(line: Int, position: Int, message: String) : TangaraError(line, position, message)