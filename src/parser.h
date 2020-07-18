#include <stdio.h>
#include <stdbool.h>
#include "utils\strlist.h"
#include "Platform.h"
#include "lib\TokensCreator.h"

char* appname;
Platform platform;
PlatformType target;
HeaderType header;
strbuilder code;

void error(const char* type, const char* message);
void lexerize(strbuilder prog);
int parse();
