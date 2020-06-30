#include <stdio.h>
#include "utils\strbuilder.h"
#include "lib\TokensCreator.h"

char* appname;
PlatformType target;
HeaderType header;
unsigned int line;
strbuilder code;

void error(const char* type, const char* message);
void lexerize();
int parse();
