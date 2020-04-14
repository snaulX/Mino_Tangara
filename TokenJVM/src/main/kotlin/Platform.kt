package com.snaulX.Tangara

data class Platform (
    var import_keyword: String = "import",
    var use_keyword: String = "use",
    var lib_keyword: String = "lib",
    var class_keyword: String = "class",
    var public_keyword: String = "public",
    var private_keyword: String = "private",
    var protected_keyword: String = "protected",
    var internal_keyword: String = "internal",
    var final_keyword: String = "final",
    var abstract_keyword: String = "abstract",
    var data_keyword: String = "data",
    var static_keyword: String = "static",
    var virtual_keyword: String = "virtual",
    var expect_keyword: String = "expect",
    var actual_keyword: String = "actual",
    var function_keyword: String = "fun",
    var constructor_keyword: String = "ctor",
    var funcalias_keyword: String = "funcalias",
    var typealias_keyword: String = "typealias",
    var variable_keyword: String = "var",
    var try_keyword: String = "try",
    var catch_keyword: String = "catch",
    var finally_keyword: String = "finally",
    var if_keyword: String = "if",
    var else_keyword: String = "else",
    var else_if_keyword: String = "",
    var while_keyword: String = "while",
    var do_keyword: String = "do",
    var for_keyword: String = "for",
    var foreach_keyword: String = "for",
    var const_keyword: String = "const",
    var enum_keyword: String = "enum",
    var delegate_keyword: String = "delegate",
    var interface_keyword: String = "interface",
    var operator_keyword: String = "operator",
    var struct_keyword: String = "struct",
    var annotaion_start: String = "@",
    var array_start: String = "[",
    var array_end: String = "]",
    var statement_start: String = "(",
    var statement_end: String = ")",
    var block_start: String = "{",
    var block_end: String = "}",
    var string_char: String = "\"",
    var char_char: String = "'",
    var directive_start: String = "#",
    var single_comment_start: String = "//",
    var multiline_comment_start: String = "/*",
    var multiline_comment_end: String = "*/",
    var typeof_keyword: String = "typeof",
    var is_keyword: String = "is",
    var with_keyword: String = "with",
    var switch_keyword: String = "switch",
    var case_keyword: String = "case",
    var default_keyword: String = "default",
    var break_keyword: String = "break",
    var continue_keyword: String = "continue",
    var return_keyword: String = "return",
    var yield_keyword: String = "yield",
    var include_keyword: String = "include",
    var annotation_keyword: String = "annotation",
    var collection_keyword: String = "collection",
    var override_keyword: String = "override",
    var implements_keyword: String = ":",
    var extends_keyword: String = ":",
    var expression_end: String = ";",
    var separator: String = ".",
    var expression_separator: String = ",",
    var nullable: String = "?",
    var add_operator: String = "+",
    var subtract_operator: String = "-",
    var multiply_operator: String = "*",
    var divise_operator: String = "/",
    var modulo_operator: String = "%",
    var power_operator: String = "^",
    var increment_operator: String = "++",
    var decrement_operator: String = "--",
    var assigment_operator: String = "=",
    var equals_operator: String = "==",
    var less_operator: String = "<",
    var greater_operator: String = ">",
    var addassign_operator: String = "+=",
    var subassign_operator: String = "-=",
    var mulassign_operator: String = "*=",
    var divassign_operator: String = "/=",
    var modassign_operator: String = "%=",
    var gore_operator: String = ">=",
    var lore_operator: String = "<=",
    var range_operator: String = "..",
    var not_operator: String = "!",
    var not_equals_operator: String = "!=",
    var breakpoint_keyword: String = "breakpoint",
    var new_operator: String = "new",
    var throw_operator: String = "throw",
    var goto_keyword: String = "goto",
    var in_operator: String = "in",
    var convert_operator: String = "to",
    var package_keyword: String = "package",
    var short_return_operator: String = "=>",
    var lambda_operator: String = "->",
    var after_case_operator: String = ":",
    var null_value: String = "null",
    var false_value: String = "false",
    var true_value: String = "true",
    var readonly_keyword: String = "val",
    var async_keyword: String = "async",
    var await_keyword: String = "await",
    var out_parameter: String = "out",
    var ref_keyword: String = "ref",
    var in_parameter: String = "readonly",
    var params_parameter: String = "params",
    var generic_start: String = "<",
    var generic_end: String = ">"
)