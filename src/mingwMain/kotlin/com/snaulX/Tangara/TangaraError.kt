package com.snaulX.Tangara

open class TangaraError(val line: Int, val position: Int, val message: String)

fun TangaraError.getError() = "${this::class.simpleName} in line $line position $position. $message\n"