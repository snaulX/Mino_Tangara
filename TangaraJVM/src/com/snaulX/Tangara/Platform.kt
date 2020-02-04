package com.snaulX.Tangara

import com.fasterxml.jackson.module.kotlin.*

@XmlName(name = "tokens")
data class Platform (
    @XmlName(name = "import_keyword")
    var import_keyword: String = "import",
    @XmlName(name = "use_keyword")
    var use_keyword: String = "use",
    @XmlName(name = "lib_keyword")
    var lib_keyword: String = "lib",
    @XmlName(name = "class_keyword")
    var class_keyword: String = "class",
    @XmlName(name = "public_keyword")
    var public_keyword: String = "public",
    @XmlName(name = "private_keyword")
    var private_keyword: String = "private",
    @XmlName(name = "protected_keyword")
    var protected_keyword: String = "protected",
    @XmlName(name = "sealed_keyword")
    var sealed_keyword: String = "sealed",
    @XmlName(name = "function_keyword")
    var function_keyword: String = "fun",
    @XmlName(name = "variable_keyword")
    var variable_keyword: String = "var",
    @XmlName(name = "try_keyword")
    var try_keyword: String = "try",
    @XmlName(name = "catch_keyword")
    var catch_keyword: String = "catch",
    @XmlName(name = "if_keyword")
    var if_keyword: String = "if",
    @XmlName(name = "else_keyword")
    var else_keyword: String = "else",
    @XmlName(name = "while_keyword")
    var while_keyword: String = "while",
    @XmlName(name = "do_keyword")
    var do_keyword: String = "do",
    @XmlName(name = "for_keyword")
    var for_keyword: String = "for",
    @XmlName(name = "foreach_keyword")
    var foreach_keyword: String = "foreach",
    @XmlName(name = "const_keyword")
    var const_keyword: String = "const",
    @XmlName(name = "enum_keyword")
    var enum_keyword: String = "enum",
    @XmlName(name = "interface_keyword")
    var interface_keyword: String = "interface",
    @XmlName(name = "annotation_start")
    var annotaion_start: String = "@",
    var annotaion_end: String = "",
    @XmlName(name = "array_start")
    var array_start: String = "[",
    var array_end: String = "]",
    @XmlName(name = "statement_start")
    var statement_start: String = "(",
    var statement_end: String = ")",
    @XmlName(name = "block_start")
    var block_start: String = "{",
    var block_end: String = "}",
    var string_char: String = "\"",
    var char_char: String = "'",
    @XmlName(name = "directive_start")
    var directive_start: String = "#",
    var single_comment: String = "//",
    @XmlName(name = "multiline_comment_start")
    var multiline_comment_start: String = "/*",
    var multiline_comment_end: String = "*/",
    @XmlName(name = "typeof_keyword")
    var typeof_keyword: String = "typeof",
    @XmlName(name = "is_keyword")
    var is_keyword: String = "is",
    @XmlName(name = "with_keyword")
    var with_keyword: String = "with",
    @XmlName(name = "switch_keyword")
    var switch_keyword: String = "switch",
    @XmlName(name = "case_keyword")
    var case_keyword: String = "case",
    @XmlName("default_keyword")
    var default_keyword: String = "default",
    @XmlName("break_keyword")
    var break_keyword: String = "break",
    @XmlName("continue_keyword")
    var continue_keyword: String = "continue",
    @XmlName("include_keyword")
    var include_keyword: String = "include",
    @("annotation_keyword")
    var annotation_keyword: String = "annotation",
    @("collection_keyword")
    var collection_keyword: String = "collection",
    @("override_keyword")
    var override_keyword: String = "override",
    @("implements_keyword")
    var implements_keyword: String = ":",
    @XmlName("extends_keyword")
    var extends_keyword: String = ":",
    var expression_end: String = ";",
    var float_separator: Char = '.',
    var expression_separator: String = ",",
    var add_code: String = "import std;"
)