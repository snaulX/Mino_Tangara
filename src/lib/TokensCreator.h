#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <wchar.h>
#include "HeaderType.h"
#include "PlatformType.h"
#include "ClassType.h"
#include "SecurityDegree.h"
#include "FuncType.h"
#include "VarType.h"
#include "LoopType.h"
#include "OperatorType.h"

// Work with file
void set_output(char* app_name, char* extension);
void end_work();
void writestr(const char* str);
void writewstr(wchar_t* str);

// Create Header
void set_header(HeaderType h);
void set_platform(PlatformType p);

// Write Tokens
void incline();
void create_class(const char* name, ClassType ct, SecurityDegree s);
void create_function(const char* name, const char* type_name, FuncType ft, SecurityDegree s);
void start_vardefinition(VarType vt, SecurityDegree s);
void block(bool start);
void statement(bool start);
void sequence(bool start);
void call_literal(const char* literal);
void insert_separator(bool isliteral);
void insert_exprend();
void insert_loop(LoopType lt);
void insert_label(const char* name);
void goto_label(const char* name);
void insert_loopop(bool _break, const char* label_name);
void callop(OperatorType op);
// Values
void call_null();
void call_int(int v);
void call_byte(char v);
void call_char(char v);
void call_short(short v);
void call_float(float v);
void call_long(long v);
void call_double(double v);
void call_string(wchar_t* v);
void call_bool(bool v);
// End of values
void insert_nullable();
void insert_switch();
void insert_case();
void insert_directive();
void insert_new();
void insert_annotation();
void throw_exception();
void insert_try();
void insert_catch();
void insert_finally();
void insert_if();
void insert_else();
void insert_return(bool _short);
void insert_actual(bool actual);
void check_type_of(const char* type_name);
void set_package(const char* name);
void import_library(const char* name);
void import_package(const char* name);
void include(const char* name);
void insert_breakpoint();
void impelements();
void extends();
void instanceof(const char* name);
void insert_with();
void insert_yield();
void insert_lambda(bool lambda);
void insert_async(bool async);
void parameter_type(bool out);
void insert_ref();
void insert_generic(bool start);
