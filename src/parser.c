#include "parser.h"

char* appname;
Platform platform;
PlatformType target;
HeaderType header;
unsigned int errors_count;
unsigned int line;
strbuilder code;
strlist lexemes, strings, numbers;
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
	create_sb(&lexem);
	reparse_platform();
	for (code.index = 0; code.index < code.length; code.index++)
	{
		if (isstring)
		{
			if (cur(&code) == platform.tokens[string_char])
			{
				addsb(&strings, lexem);
				clear(&lexem);
			}
			else
				
		}
		else 
		{
			if (isws(&code))
			{
				if (!isws(&lexem)) // if char before current whitespace was not whitespace
				{
					addsb(&lexemes, lexem);
					clear(&lexem);
				}
			}
			else if (isdgt(&code))
			{
				//pass
			}
			else if (isltr(&code))
			{
				append(&lexem, cur(&code));
			}
			else
			{
				//punctuation
			}
		}
	}
	clear(&lexem);
}
int parse()
{
	set_output(appname, ".tokens");
	set_platform(target);
	set_header(header);
	create_list(&lexemes);
	create_list(&strings);
	create_list(&numbers);
	lexerize(code);
	clear(&lexemes);
	clear(&strings);
	clear(&numbers);
	if (errors_count > 0)
		return -1;
	else
	{
		printf("PARSING SUCCESFUL\n");
		return 0;
	}
}
