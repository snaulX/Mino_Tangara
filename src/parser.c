#include "parser.h"

char* appname;
PlatformType target;
HeaderType header;
unsigned int errors_count;
unsigned int line;
strbuilder code;
strlist lexemes;
bool isstring = false;

void error(const char* type, const char* message)
{
	printf("%sError in line %u. %s.\n", type, line, message);
	errors_count++;
}
void reparse_platform()
{
	//pass
}
void import(char* name)
{
	//pass
}
void lexerize(strbuilder prog)
{
	strbuilder lexem;
	create_sb(&lexem, 0);
	reparse_platform();
	while (code.index < code.length)
	{
		if (isws(&code))
		{
			//pass
		}
		else if (isdgt(&code))
		{
			//pass
		}
		else if (isltr(&code))
		{
			//pass
		}
		else
		{
			//pass
		}
		code.index++;
	}
	clear(&lexem);
}
int parse()
{
	set_output(appname, ".tokens");
	set_platform(target);
	set_header(header);
	create_list(&lexemes);
	lexerize(code);
	clear(&lexemes);
	if (errors_count > 0)
		return -1;
	else
	{
		printf("PARSING SUCCESFUL\n");
		return 0;
	}
}
