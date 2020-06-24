#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <wchar.h>
#include "TokensCreator.h"

int main(int argc, char* argv[])
{
    printf(argc);
    if (argc == 0)
    {
        printf(sizeof(wchar_t));
        printf("Tangara 2020-2020\nAuthor: snaulX\nAll copyrights reserved.\nGitHub repository: https://github.com/mino-lang/Tangara\nFor get commands write -h or --help");
    }
    else
    {
        int a;
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
        }
    }
    return 0;
}
