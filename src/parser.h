#include <stdio.h>
#include <stdbool.h>
#include "lib\TokensCreator.h"
#include "utils\strlist.h"

char* appname;
PlatformType target;
HeaderType header;
strbuilder code;

void error(const char* type, const char* message);
void lexerize(strbuilder prog);
int parse();
