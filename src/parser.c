#include "lextpl.h"

#define putlexem() { addsb(&lexemes, lexem); clear(&lexem); }

char* appname;
PlatformType target;
HeaderType header;
unsigned int errors_count, line;
char isnumb; // can have so small values
strbuilder code;
strlist lexemes, strings;
bool isstring = false, isliteral = false;

void error(const char* type, const char* message)
{
	printf("%sError in line %u. %s.\n", type, line, message);
	errors_count++;
}
void lexerize(strbuilder prog)
{
	strbuilder lexem;
	create_sb(&lexem);
	for (code.index = 0; code.index < code.length; code.index++)
	{
		skipws(&code);
		if (isdgt(&code))
		{
			// is number
		}
		else if (isltr(&code))
		{
			isliteral = true;
		}
		else 
		{
			switch (cur(&code))
			{
				case '"':
					isstring = !isstring;
					break;
				case '\'':
					// is char
					code.index++;
					char ch = cur(&code);
					break;
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
	lexerize(code);
	lclear(&lexemes);
	lclear(&strings);
	if (errors_count == 0)
		printf("PARSING SUCCESFUL\n");
	return errors_count;
}
