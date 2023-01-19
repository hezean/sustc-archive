#pragma once

#include <cstddef>
#include <initializer_list>
#include <iostream>
#include <ostream>
#include <string>

#include "../util/cnnexception.hpp"
#include "../util/imgdata.hpp"
#include "../util/extras.hpp"

using std::initializer_list;
using std::ostream;
using std::string;
using std::vector;

class CnnLayer {
public:
    virtual ~CnnLayer() = default;

    virtual Img forward(const Img &img) const = 0;

    virtual void summary(ostream &os,
                         const vector<size_t> &input) const = 0;

    virtual bool checkValid(vector<size_t> &last_opt) const = 0;

    virtual vector<size_t> optSize(vector<size_t> &in_size) const = 0;

};
