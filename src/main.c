#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <wchar.h>
#include <locale.h>
#include "parser.h"
#include "utils\utils.h"

int main(int argc, char* argv[])
{
    if (argc == 1)
    {
        printf("Tangara 2020-2020\nAuthor: snaulX\nAll copyrights reserved.\nGitHub repository: https://github.com/mino-lang/Tangara\nFor get commands write -h or --help");
        return 0;
    }
    else
    {
        /*int a;
        while ((a = getopt(argc, argv, "h:")) != -1)
        {
            switch (a)
            {
            case 'h':
                break;
            case '?':
                break;
            default:
                return 0;
            }
        }*/
        setlocale(LC_ALL, NULL);
        FILE* fprog;
        wint_t wc;
        fprog = fopen(argv[1], "r");
        if (fprog == NULL)
        	error("ProgramNotFound", "File for parsing not found");
        create_sb(&code);
        while((wc = fgetwc(fprog)) != WEOF) 
		{
            append(&code, wc);
        }
        fclose(fprog);
        target = Common;
        header = Script;
        appname = removeext(argv[1]);
        return parse();
    }
}
