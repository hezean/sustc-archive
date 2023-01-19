#include <algorithm>
#include <iostream>
#include <string>

using namespace std;

struct stuinfo {
    char name[20];
    double score[3];
    double ave;
};

void inputstu(stuinfo*, int);
void showstu(stuinfo*, int);
void sortstu(stuinfo*, int);
bool findstu(stuinfo*, int, char*);
