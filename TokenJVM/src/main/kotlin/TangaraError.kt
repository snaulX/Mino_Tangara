package com.snaulX.Tangara

open class TangaraError(val line: Int, val message: String) {
    override fun toString(): String = "${this::class.simpleName} in line $line. $message\n"
}