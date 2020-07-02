#include "strlist.h"

void create_list(strlist* list)
{
	//pass
}
void create_lenlist(strlist* list, unsigned int len)
{
	(*list).strs = (wchar_t*)malloc(len * sizeof(wchar_t));
	(*list).length = len;
}
void add(strlist* l, wchar_t* s)
{
	//pass
}
void addsb(strlist* l, strbuilder sb)
{
	//pass
}
void lclear(strlist* l)
{
	free((*l).strs);
	(*l).index = 0;
	(*l).length = 0;
}
void lremove(strlist* l)
{
	free((*l).strs[(*l).index]);
	(*l).index--;
	(*l).length--;
}
wchar_t* lcur(strlist* l)
{
	//pass
}
