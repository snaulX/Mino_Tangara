#include "lexer.h"

strlist lexerize(strbuilder prog)
{
	strbuilder lexem;
	strlist lexemes;
	create_list(&lexemes);
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
	return lexemes;
}
