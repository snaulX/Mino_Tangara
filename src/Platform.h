#include <stdio.h>
#include <stdlib.h>
#include <wchar.h>
#include <string.h>
#include "lib/cJSON.h"
#include "utils/strlist.h"
#include "utils/utils.h"

#define import_keyword 0
#define use_keyword 1
#define lib_keyword 2
#define class_keyword 3
#define public_keyword 4
#define private_keyword 5
#define protected_keyword 6
#define internal_keyword 7
#define final_keyword 8
#define abstract_keyword 9
#define data_keyword 10
#define static_keyword 11
#define virtual_keyword 12
#define expect_keyword 13
#define actual_keyword 14
#define function_keyword 15
#define constructor_keyword 16
#define funcalias_keyword 17
#define typealias_keyword 18
#define variable_keyword 19
#define try_keyword 20
#define catch_keyword 21
#define finally_keyword 22
#define if_keyword 23
#define else_keyword 24
#define else_if_keyword 25
#define while_keyword 26
#define do_keyword 27
#define for_keyword 28
#define foreach_keyword 29
#define const_keyword 30
#define enum_keyword 31
#define delegate_keyword 32
#define interface_keyword 33
#define operator_keyword 34
#define struct_keyword 35
#define annotation_start 36
#define array_start 37
#define array_end 38
#define statement_start 39
#define statement_end 40
#define block_start 41
#define block_end 42
#define string_char 43
#define char_char 44
#define directive_start 45
#define single_comment_start 46
#define multi_comment_start 47
#define multi_comment_end 48
#define typeof_keyword 49
#define is_keyword 50
#define with_keyword 51
#define switch_keyword 52
#define case_keyword 53
#define default_keyword 54
#define break_keyword 55
#define continue_keyword 56
#define return_keyword 57
#define yield_keyword 58
#define include_keyword 59
#define annotation_keyword 60
#define collection_keyword 61
#define override_keyword 62
#define implements_keyword 63
#define extends_keyword 64
#define expression_end 65
#define separator 66
#define expression_separator 67
#define nullable 68
#define add_operator 69
#define subtract_operator 70
#define multiply_operator 71
#define divise_operator 72
#define modulo_operator 73
#define power_operator 74
#define increment_operator 75
#define decrement_operator 76
#define assigment_operator 77
#define equals_operator 78
#define less_operator 79
#define greater_operator 80
#define addassign_operator 81
#define subassign_operator 82
#define mulassign_operator 83
#define divassign_operator 84
#define modassign_operator 85
#define gore_operator 86
#define lore_operator 87
#define range_operator 88
#define not_operator 89
#define not_equals_operator 90
#define breakpoint_keyword 91
#define new_operator 92
#define throw_operator 93
#define goto_keyword 94
#define in_operator 95
#define convert_operator 96
#define short_return_keyword 97
#define lambda_operator 98
#define after_case_operator 99
#define null_value 100
#define false_value 101
#define true_value 102
#define readonly_keyword 103
#define async_keyword 104
#define await_keyword 105
#define out_parameter 106
#define ref_keyword 107
#define in_parameter 108
#define params_parameter 109
#define generic_start 110
#define generic_end 111

typedef struct {
    wchar_t* tokens[113];
} Platform;

strlist puncts; // punctuation keywords

Platform new_platform();

void reparse_platform();

void import(char* name);
