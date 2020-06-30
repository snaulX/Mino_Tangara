#include <stdio.h>
#include <wchar.h>

typedef struct {
	wint_t* buffer;
	unsigned int length;
} strbuilder;
