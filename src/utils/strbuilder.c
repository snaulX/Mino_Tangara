#include "strbuilder.h"

void create_sb(strbuilder* sb, unsigned int len)
{
	(*sb).buffer = (wchar_t*)calloc(len, sizeof(wchar_t));
	(*sb).length = len;
	(*sb).index = 0;
}
void clear(strbuilder* sb)
{
	free((*sb).buffer);
	(*sb).length = 0;
	(*sb).index = 0;
}
void cleanprev(strbuilder* sb)
{
	//pass
}
void append(strbuilder* sb, wchar_t c)
{
	(*sb).index++;
	(*sb).buffer[(*sb).index] = c;
}
