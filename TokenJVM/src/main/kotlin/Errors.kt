package com.snaulX.Tangara

class ImportError(line: Int, message: String) : TangaraError(line, message)

class IncludeError(line: Int, message: String) : TangaraError(line, message)

class UseError(line: Int, message: String) : TangaraError(line, message)

class LibError(line: Int, message: String) : TangaraError(line, message)

class SyntaxError(line: Int, message: String) : TangaraError(line, message)