#include "parser.h"

char* appname;
Platform platform;
PlatformType target;
HeaderType header;
unsigned int errors_count, line, isnumb;
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
			{
				append(&lexem, cur(&code));
			}	
		}
		else if (isnumb)
		{
			if (isdgt(&code))
				append(&lexem, cur(&code));
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
				append(&lexem, cur(&code));
				isnumb = true;
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
	platform = new_platform();
	lexerize(code);
	lclear(&lexemes);
	lclear(&strings);
	lclear(&numbers);
	if (errors_count > 0)
		return -1;
	else
	{
		printf("PARSING SUCCESFUL\n");
		return 0;
	}
}
