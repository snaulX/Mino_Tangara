#include "Platform.h"

strlist puncts; // punctuation keywords

Platform new_platform()
{
	Platform pl = { {
    	L"import",
    	L"use",
    	L"lib",
    	L"class",
    	L"public",
    	L"private",
    	L"protected",
    	L"internal",
    	L"final",
    	L"abstract",
    	L"data",
    	L"static",
    	L"virtual",
    	L"expect",
    	L"actual",
    	L"fun",
    	L"ctor",
    	L"funcalias",
    	L"typealias",
    	L"var",
    	L"try",
    	L"catch",
    	L"finally",
    	L"if",
    	L"else",
    	L"",
    	L"while",
    	L"do",
    	L"for",
    	L"for",
    	L"const",
    	L"enum",
    	L"delegate",
    	L"interface",
    	L"operator",
    	L"struct",
    	L"@",
    	L"[",
    	L"]",
    	L"(",
    	L")",
    	L"{",
    	L"}",
    	L"\"",
    	L"'",
    	L"#",
    	L"//",
    	L"/*",
    	L"*/",
    	L"typeof",
    	L"is",
    	L"with",
    	L"switch",
    	L"case",
    	L"default",
    	L"break",
    	L"continue",
    	L"return",
    	L"yield",
    	L"include",
    	L"annotation",
    	L"collection",
    	L"override",
    	L":",
    	L":",
    	L";",
    	L".",
    	L",",
    	L"?",
    	L"+",
    	L"-",
    	L"*",
    	L"/",
    	L"%",
    	L"^",
    	L"++",
    	L"--",
    	L"=",
    	L"==",
    	L"<",
    	L">",
    	L"+=",
    	L"-=",
    	L"*=",
    	L"/=",
    	L"%=",
    	L">=",
    	L"<=",
    	L"..",
    	L"!",
    	L"!=",
    	L"breakpoint",
    	L"new",
    	L"throw",
    	L"goto",
    	L"in",
    	L"to",
    	L"package",
    	L"=>",
    	L"->",
    	L":",
    	L"null",
    	L"false",
    	L"true",
    	L"val",
    	L"async",
    	L"await",
    	L"out",
    	L"ref",
    	L"readonly",
    	L"params",
    	L"<",
    	L">"
	} };
	return pl;
}

void reparse_platform()
{
	//pass
}

void import(char* name)
{
	//wchar_t* fpl = readfile(strcat(name, ".json"))->buffer;
	//char * out = new char[wcslen(fpl)+1];
	//wcstombs_s(NULL, out, wcslen(fpl)+1,vIn, wcslen(fpl)+1);	
	FILE* f;
    int c;
    f = fopen(name, "r");
    if (f == NULL)
        error("FileNotFound", strcat(strcat("File with name '", name), "' not found"));
    char* out;
    while((c = fgetc(f)) != EOF) 
	{
        //pass
    }
    fclose(f);
	cJSON* json = cJSON_Parse(out);
}
