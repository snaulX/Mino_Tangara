package com.snaulX.Tangara

import com.snaulX.TokensAPI.*

enum class Identifer(val classType: ClassType, val funcType: FuncType?) {
    DEFAULT(ClassType.DEFAULT, FuncType.DEFAULT),
    STATIC(ClassType.STATIC, FuncType.STATIC),
    FINAL(ClassType.FINAL, FuncType.FINAL),
    ABSTRACT(ClassType.ABSTRACT, FuncType.ABSTRACT),
    DATA(ClassType.DATA, null),
    ENUM(ClassType.ENUM, null)
}