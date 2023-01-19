#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <sstream>

void check_input(std::ifstream &, std::ifstream &);

size_t file_rows(std::ifstream &);

size_t file_cols(std::ifstream &);
