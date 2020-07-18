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
strbuilder* readfile(char* name)
{
	FILE* f;
    wint_t wc;
    f = fopen(name, "r");
    if (f == NULL)
        error("FileNotFound", strcat(strcat("File with name '", name), "' not found"));
    strbuilder* out;
    create_sb(out);
    while((wc = fgetwc(f)) != WEOF) 
	{
        append(out, wc);
    }
    fclose(f);
    return out;
}
