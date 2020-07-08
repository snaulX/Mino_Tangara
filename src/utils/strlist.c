#include "strlist.h"

void create_list(strlist* list)
{
	//pass
}
void create_lenlist(strlist* list, unsigned int len)
{
	list->strs = (strbuilder*) calloc(len, sizeof(strbuilder));
	int i;
	for (i = 0; i < len; i++)
	{
		create_sb(&list->strs[i]);
	}
	list->length = len;
}
void add(strlist* l, wchar_t* s)
{
	strbuilder str;
	create_lensb(&str, sizeof(s) / sizeof(wchar_t));
	str.buffer = s;
	addsb(l, str);
}
void addsb(strlist* l, strbuilder sb)
{
	l->index++;
	if (l->index < l->length) l->strs[l->index] = sb;
	else
	{
		strbuilder* old = l->strs;
		create_lenlist(l, ++l->index);
		int i;
		for (i = 0; i < sizeof(old)/sizeof(strbuilder); i++)
			addsb(l, old[i]);
		addsb(l, sb);
	}
}
void lclear(strlist* l)
{
	free(l->strs);
	l->index = 0;
	l->length = 0;
}
void lremove(strlist* l)
{
	free(lcurptr(l));
	l->index--;
	l->length--;
}
strbuilder lcur(strlist* l)
{
	return l->strs[l->index];
}
strbuilder* lcurptr(strlist* l)
{
	return &l->strs[l->index];
}
