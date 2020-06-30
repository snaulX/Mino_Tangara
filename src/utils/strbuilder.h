#include <stdio.h>
#include <wchar.h>
#include <malloc.h>
#include "..\parser.h"

typedef struct {
	wchar_t* buffer;
	unsigned int length;
	unsigned int index;
} strbuilder;

void create_sb(strbuilder* sb, unsigned int len);
void clear(strbuilder* sb);
void cleanprev(strbuilder* sb);
void append(strbuilder* sb, wchar_t c);
