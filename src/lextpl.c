#include "lextpl.h"

char* lx_security()
{
	char* reg;
	sprintf(reg, "(%s|%s|%s|%s)\\s", 
		platform.tokens[public_keyword], 
		platform.tokens[private_keyword],
		platform.tokens[protected_keyword],
		platform.tokens[internal_keyword]);
	return reg;
}
