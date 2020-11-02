#include <stdio.h>
#include <wchar.h>
#include <malloc.h>
#include <stdbool.h>

typedef struct {
	wchar_t* buffer;
	unsigned int length;
	unsigned int index;
} strbuilder;

void create_sb(strbuilder* sb);
void create_lensb(strbuilder* sb, unsigned int len);
void clear(strbuilder* sb);
// Append char
void append(strbuilder* sb, wchar_t c);
// Append string
void appendstr(strbuilder* sb, wchar_t* str);
// Get current char
wchar_t cur(strbuilder* sb);
// Get pointer on current char
wchar_t* curptr(strbuilder* sb);
// Current char is whatespace
bool isws(strbuilder* sb);
// Current char is digit
bool isdgt(strbuilder* sb);
// Current char is letter or underscore
bool isltr(strbuilder* sb);
// Remove char in sb with current index
void sbremove(strbuilder* sb);
// Skip whitespaces and update index and give value - is the end of strbuilder?
bool skipws(strbuilder* sb);
