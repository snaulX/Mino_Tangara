#include <stdio.h>
#include <wchar.h>
#include <malloc.h>
#include "strbuilder.h"

typedef struct {
	wchar_t* strs;
	unsigned int index;
	unsigned int length;
} strlist;

void create_list(strlist* list);
void create_lenlist(strlist* list, unsigned int len);
void add(strlist* l, wchar_t* s);
void addsb(strlist* l, strbuilder sb);
void lclear(strlist* l);
void lremove(strlist* l);
wchar_t* lcur(strlist* l);
