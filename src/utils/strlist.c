#include "strlist.h"

void create_list(strlist* list)
{
	//pass
}
void create_lenlist(strlist* list, unsigned int len)
{
	(*list).strs = (strbuilder*) calloc(len, sizeof(strbuilder));
	int i;
	for (i = 0; i < len; i++)
	{
		create_sb(&(*list).strs[i], 0);
	}
	(*list).length = len;
}
void add(strlist* l, wchar_t* s)
{
	strbuilder str;
	create_sb(&str, sizeof(s) / sizeof(wchar_t));
	str.buffer = s;
	addsb(l, str);
}
void addsb(strlist* l, strbuilder sb)
{
	int ind = (*l).index;
	if (ind >= (*l).length)
	{
		strbuilder* last;
		last = (*l).strs;
		free((*l).strs);
		if ((*l).index > (*l).length)
		{
			(*l).strs = calloc(ind, sizeof(strbuilder) * ind);
			(*l).length = ind;
		}
		else
		{	
			(*l).strs = calloc(ind + 1, sizeof(strbuilder) * (ind + 1));
			(*l).length = ind + 1;
		}
		int i;
		ind = (*l).length;
		for (i = 0; i < ind; i++)
		{
			(*l).strs[i] = last[i];
		}
		(*l).strs[ind - 1] = sb;
	}
	else
	{
		(*l).strs[ind] = sb;
	}
}
void lclear(strlist* l)
{
	free((*l).strs);
	(*l).index = 0;
	(*l).length = 0;
}
void lremove(strlist* l)
{
	free(&(*l).strs[(*l).index]);
	(*l).index--;
	(*l).length--;
}
strbuilder lcur(strlist* l)
{
	return (*l).strs[(*l).index];
}
strbuilder* lcurptr(strlist* l)
{
	return &(*l).strs[(*l).index];
}
