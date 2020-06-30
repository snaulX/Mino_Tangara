#include "parser.h"

unsigned int line;

void error(const char* type, const char* message)
{
	printf("%sError in line %u. %s.\n", type, line, message);
}
