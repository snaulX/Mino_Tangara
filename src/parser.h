#include <stdio.h>
#include "utils\strbuilder.h"
#include "lib\TokensCreator.h"

char* appname;
PlatformType target;
HeaderType header;
strbuilder code;

void error(const char* type, const char* message);
void reparse_platform();
void import(char* name);
strbuilder lexerize();
int parse();
