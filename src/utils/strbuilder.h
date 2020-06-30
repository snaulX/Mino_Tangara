#include <stdio.h>
#include <wchar.h>
#include <malloc.h>
#include <stdbool.h>

typedef struct {
	wchar_t* buffer;
	unsigned int length;
	unsigned int index;
} strbuilder;

void create_sb(strbuilder* sb, unsigned int len);
void clear(strbuilder* sb);
void append(strbuilder* sb, wchar_t c);
// Get current char
wchar_t cur(strbuilder* sb);
// Current char is whatespace
bool isws(strbuilder* sb);
// Current char is digit
bool isdigit(strbuilder* sb);
