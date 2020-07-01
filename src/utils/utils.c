#include "utils.h"

char* removeext(char* fn)
{
	int i;
	char* fnwoext; // filename without extension
	for (i = strlen(fn); i > 0; i--)
	{
		if (fn[i] == '.')
			return strncpy(fnwoext, fn, i);		
	}
	return fnwoext;
}
