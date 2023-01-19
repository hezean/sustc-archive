#include "stuinfo.hpp"

void inputstu(stuinfo stu[], int n) {
    cout << "Please input the info of" << n << "students:" << endl;
    for (int i = 0; i < n; i++) {
        cout << "Student " << i << "'s name:";
        cin.getline(stu[i].name, 20, '\n');
        cout << "Student " << i << "'s scores:";
        double tmp = 0;
        for (int j = 0; j < 3; j++) {
            cin >> stu[i].score[j];
            tmp += stu[i].score[j];
        }
        stu[i].ave = tmp / 3;
        cin.get();
    }
}

void showstu(stuinfo stu[], int n) {
    cout << "The information of " << n << " students you input are:" << endl;
    for (size_t i = 0; i < n; i++) {
        cout << "Student " << i << "'s name:" << stu[i].name << ",scores:";
        for (size_t j = 0; j < 3; j++) {
            cout << stu[i].score[j] << " ";
        }
        cout << endl;
    }
}

void sortstu(stuinfo stu[], int n) {
    sort(stu, stu + n, [](stuinfo a, stuinfo b) { return (a.ave > b.ave); });

    cout << "The descending order of the students:" << endl;
    for (size_t i = 0; i < n; i++) {
        cout << "Student " << i << "'s name:" << stu[i].name;
        cout << "average: " << stu[i].ave;
        cout << endl;
    }
}

bool findstu(stuinfo stu[], int n, char ch[]) {
    for (size_t i = 0; i < n; i++) {
        if (!string(stu[i].name).compare(ch)) {
            cout << ch << " is in the list." << endl;
            return true;
        }
    }
    cout << ch << " is not in the list" << endl;
    return false;
}
