#include "strbuilder.h"

void create_sb(strbuilder* sb)
{
	//pass
}
void create_lensb(strbuilder* sb, unsigned int len)
{
	sb->buffer = (wchar_t*)calloc(len, sizeof(wchar_t));
	sb->length = len;
	sb->index = 0;
}
void clear(strbuilder* sb)
{
	free(sb->buffer);
	sb->length = 0;
	sb->index = 0;
}
void append(strbuilder* sb, wchar_t c)
{
	sb->index++;
	if (sb->index < sb->length) sb->buffer[sb->index] = c;
	else
	{
		wchar_t* old = sb->buffer;
		create_lensb(sb, ++sb->index);
		int i;
		for (i = 0; i < sizeof(old)/sizeof(wchar_t); i++)
			append(sb, old[i]);
		append(sb, c);
	}
}
wchar_t cur(strbuilder* sb)
{
	return sb->buffer[sb->index];
}
wchar_t* curptr(strbuilder* sb)
{
	return &sb->buffer[sb->index];
}
bool isws(strbuilder* sb)
{
	return iswspace(cur(sb));
}
bool isdgt(strbuilder* sb)
{
	return iswdigit(cur(sb));
}
bool isltr(strbuilder* sb)
{
	return iswalpha(cur(sb)) || cur(sb) == '_';
}
void sbremove(strbuilder* sb)
{
	free(curptr(sb));
}
