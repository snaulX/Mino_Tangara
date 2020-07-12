#include "parser.h"

char* appname;
Platform platform;
PlatformType target;
HeaderType header;
unsigned int errors_count, line;
char isnumb; // can have so small values
strbuilder code;
strlist lexemes, strings;
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
		// TODO: Must collect chars for string end becouse
		// platform.tokens[string_char] can have a lot of chars (not one) 
		wchar_t cc = cur(&code); 
		if (isstring)
		{
			if (cc == platform.tokens[string_char])
			{
				call_string(lexem.buffer);
				clear(&lexem);
				isstring = false;
			}
			else
			{
				append(&lexem, cc);
			}	
		}
		else if (isnumb)
		{
			if (isdgt(&code))
				append(&lexem, cc);
			else if (cc == '.')
			{
				if (isnumb == 1) // number had dot?
				{
					if (cur(&lexem) == '.')
					{
						sbremove(&lexem);
						call_double(wcstod(lexem.buffer, NULL));
						clear(&lexem);
						append(&lexem, '.');
					}
					else
					{
						call_double(wcstod(lexem.buffer, NULL));
						clear(&lexem);
					}
					append(&lexem, '.');
					isnumb = false;
				}
				else
				{
					isnumb = 1;
				}
			}
			else if (cc == 'b')
			{
				//byte
				if (isnumb == 1)
					error("NumberFormat", "Byte number can`t have dot");
				else
					call_byte((char)wcstol(lexem.buffer, NULL, 10));
				clear(&lexem);
			}
			else if (cc == 's')
			{
				//short
				if (isnumb == 1)
					error("NumberFormat", "Short number can`t have dot");
				else
					call_short((short)wcstol(lexem.buffer, NULL, 10));
				clear(&lexem);
			}
			else if (cc == 'l')
			{
				//long
				if (isnumb == 1)
					error("NumberFormat", "Long number can`t have dot");
				else
					call_long(wcstol(lexem.buffer, NULL, 10));
				clear(&lexem);
			}
			else if (cc == 'f')
			{
				//float
				call_float((float)wcstod(lexem.buffer, NULL));
				clear(&lexem);
			}
			else if (cc == 'd')
			{
				//double
				call_double(wcstod(lexem.buffer, NULL));
				clear(&lexem);
			}
			else
			{
				call_int((short)wcstol(lexem.buffer, NULL, 10));
				clear(&lexem);
				append(&lexem, cc);
			}
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
				// TODO: Fix bug when literal gh232j will parse wrong
				append(&lexem, cc);
				isnumb = true;
			}
			else if (isltr(&code))
			{
				append(&lexem, cc);
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
	platform = new_platform();
	lexerize(code);
	lclear(&lexemes);
	lclear(&strings);
	if (errors_count == 0)
		printf("PARSING SUCCESFUL\n");
	return errors_count;
}
