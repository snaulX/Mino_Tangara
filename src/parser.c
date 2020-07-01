#include "parser.h"

char* appname;
PlatformType target;
HeaderType header;
unsigned int errors_count;
unsigned int line;
strbuilder code;

void error(const char* type, const char* message)
{
	printf("%sError in line %u. %s.\n", type, line, message);
	errors_count++;
}
void lexerize()
{
	//pass
}
int parse()
{
	set_output(appname, ".tokens");
	set_platform(target);
	set_header(header);
	lexerize();
	if (errors_count > 0)
		return -1;
	else
	{
		printf("PARSING SUCCESFUL\n");
		return 0;
	}
}
