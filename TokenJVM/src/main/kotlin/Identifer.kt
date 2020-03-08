package com.snaulX.Tangara

import com.snaulX.TokensAPI.*

enum class Identifer(val classType: ClassType?, val funcType: FuncType?, val varType: VarType?) {
    DEFAULT(ClassType.DEFAULT, FuncType.DEFAULT, VarType.DEFAULT),
    STATIC(ClassType.STATIC, FuncType.STATIC, VarType.STATIC),
    FINAL(ClassType.FINAL, FuncType.FINAL, VarType.FINAL),
    ABSTRACT(ClassType.ABSTRACT, FuncType.ABSTRACT, VarType.ABSTRACT),
    DATA(ClassType.DATA, null, null),
    ENUM(ClassType.ENUM, null, null),
    VIRTUAL(null, FuncType.VIRTUAL, VarType.VIRTUAL),
    OVERRIDE(null, FuncType.OVERRIDE, VarType.OVERRIDE)
}