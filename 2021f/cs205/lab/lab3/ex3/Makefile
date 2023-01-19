SRC_PATH = .
SRC = $(wildcard $(SRC_PATH)/*.cpp)
OBJ = $(patsubst %.cpp,%.o,$(SRC))
TAR = ex3
INC = -I .

CC = g++
CXXFLAGS = -c -Wall -std=c++20

$(TAR): $(OBJ)
	$(CC) -o $@ $(OBJ)

%.o: %.cpp
	$(CC) $(CXXFLAGS) $< -o $@ $(INC)


.PHONY: clean
clean:
	rm -rf $(SRC_PATH)/*.o $(TAR)