#include <math.h>

#include <iostream>

void displaySquare(int side = 4, char filledCharacter = '*') {
    for (int i = 0; i < side; i++) {
        for (int j = 0; j < side; j++) {
        std::cout << filledCharacter;
        }
        std::cout << std::endl;
    }
}

int main() {
    displaySquare(5, '#');
    displaySquare(5);
    displaySquare();
}
