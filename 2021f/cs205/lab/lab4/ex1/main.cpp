#include "stuinfo.hpp"

#define LEN 3
 
int main() {
    auto stu = new stuinfo[LEN];
    inputstu(stu, LEN);
    showstu(stu, LEN);
    sortstu(stu, LEN);
    char ss[20];
    cout << "Enter the name you want to find" << endl;
    cin.getline(ss, 20);
    findstu(stu, LEN, ss);
    delete[] stu;
}
