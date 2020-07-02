#include <stdio.h>
#include "lexer.h"
#include "lib\TokensCreator.h"

char* appname;
PlatformType target;
HeaderType header;
strbuilder code;

void error(const char* type, const char* message);
void reparse_platform();
void import(char* name);
int parse();
