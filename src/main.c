#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "TokensCreator.h"

int main(int argc, char* argv[])
{
    if (argc == 0)
    {
        printf("Tangara\nParser for Mino\nAuthor: snaulX\n2020 (c)");
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
