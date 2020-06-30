#include "TokensCreator.h"

FILE* output;

void set_output(char* app_name, char* extension)
{
	output = fopen(strcat(app_name, extension), "wb");
}

void end_work()
{
	fclose(output);
}

void writestr(const char* str)
{
	int l = strlen(str);
	putc(l, output);
	int i;
	for (i = 0; i < l; i++)
	{
		putc(str[i], output);
	}
}

void set_header(HeaderType h)
{
	putc(h, output);
}

void set_platform(PlatformType p)
{
	putc(p, output);
}

void incline()
{
	putc(0, output);
}
void create_class(const char* name, ClassType ct, SecurityDegree s)
{
	putc(1, output);
	writestr(name);
	putc(ct, output);
	putc(s, output);
}
void create_function(const char* name, const char* type_name, FuncType ft, SecurityDegree s)
{
	putc(2, output);
	writestr(name);
	writestr(type_name);
	putc(ft, output);
	putc(s, output);
}
void start_vardefinition(VarType vt, SecurityDegree s)
{
	putc(3, output);
	putc(vt, output);
	putc(s, output);
}
void block(bool start)
{
	putc(4, output);
	putc(start, output);
}
void statement(bool start)
{
	putc(5, output);
	putc(start, output);
}
void sequence(bool start)
{
	putc(5, output);
	putc(start, output);
}
void call_literal(const char* literal)
{
	putc(6, output);
	writestr(literal);
}
void insert_separator(bool isliteral)
{
	putc(7, output);
	putc(isliteral, output);
}
void insert_exprend()
{
	putc(8, output);
}
void insert_loop(LoopType lt)
{
	putc(9, output);
	putc(lt, output);
}
void insert_label(const char* name)
{
	putc(10, output);
	writestr(name);
}
void goto_label(const char* name)
{
	putc(11, output);
	writestr(name);
}
void insert_loopop(bool _break, const char* label_name)
{
	putc(12, output);
	putc(_break, output);
	writestr(label_name);
}
void callop(OperatorType op)
{
	putc(13, output);
	putc(op, output);
}
// Values
void call_null()
{
	putc(14, output);
}
void call_int(int v)
{
	putc(15, output);
	putc(v, output);
}
void call_byte(char v)
{
	putc(16, output);
	putc(v, output);
}
void call_char(char v)
{
	putc(17, output);
	putc(v, output);
}
void call_short(short v)
{
	putc(18, output);
	putc(v, output);
}
void call_float(float v)
{
	putc(19, output);
	putc(v, output);
}
void call_long(long v)
{
	putc(20, output);
	putc(v, output);
}
void call_double(double v)
{
	putc(21, output);
	putc(v, output);
}
void call_string(const char* v)
{
	putc(22, output);
	writestr(v);
}
void call_bool(bool v)
{
	putc(23, output);
	putc(v, output);
}
// End of values
void insert_nullable()
{
	putc(24, output);
}
void insert_switch()
{
	putc(25, output);
}
void insert_case()
{
	putc(26, output);
}
void insert_directive()
{
	putc(27, output);
}
void insert_new()
{
	putc(28, output);
}
void insert_annotation()
{
	putc(29, output);
}
void throw_exception()
{
	putc(30, output);
}
void insert_try()
{
	putc(31, output);
}
void insert_catch()
{
	putc(32, output);
}
void insert_finally()
{
	putc(33, output);
}
void insert_if()
{
	putc(34, output);
}
void insert_else()
{
	putc(35, output);
}
void insert_return(bool _short)
{
	putc(36, output);
	putc(_short, output);
}
void insert_actual(bool actual)
{
	putc(37, output);
	putc(actual, output);
}
void check_type_of(const char* type_name)
{
	putc(38, output);
	writestr(type_name);
}
void set_package(const char* name)
{
	putc(39, output);
	writestr(name);
}
void import_library(const char* name)
{
	putc(40, output);
	writestr(name);
}
void import_package(const char* name)
{
	putc(41, output);
	writestr(name);
}
void include(const char* name)
{
	putc(42, output);
	writestr(name);
}
void insert_breakpoint()
{
	putc(43, output);
}
void impelements()
{
	putc(44, output);
}
void extends()
{
	putc(45, output);
}
void instanceof(const char* name)
{
	putc(46, output);
	writestr(name);
}
void insert_with()
{
	putc(47, output);
}
void insert_yield()
{
	putc(48, output);
}
void insert_lambda(bool lambda)
{
	putc(49, output);
	putc(lambda, output);
}
void insert_async(bool async)
{
	putc(50, output);
	putc(async, output);
}
void parameter_type(bool out)
{
	putc(51, output);
	putc(out, output);
}
void insert_ref()
{
	putc(52, output);
}
void insert_generic(bool start)
{
	putc(53, output);
}
