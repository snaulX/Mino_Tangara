#include <stdio.h>
#include <stdbool.h>
#include "utils\strlist.h"
#include "lib\TokensCreator.h"

char* appname;
PlatformType target;
HeaderType header;
strbuilder code;

void error(const char* type, const char* message);
void lexerize(strbuilder prog);
void reparse_platform();
void import(char* name);
int parse();
