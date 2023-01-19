#pragma once

#include <fstream>
#include "../util/cnnexception.hpp"

struct Data {
    size_t width;
    size_t height;
    size_t channels;
    int ref_cnt;
    float *data;

    ~Data() {
        try {
            delete[] data;
            data = nullptr;
        } catch (...) {}
    }
};

class Img {
    Data *m_data;
public:

    Img(size_t w, size_t h, size_t c) : m_data(nullptr) {
        if (w <= 0 || h <= 0 || c <= 0) throw InvalidArgs("Invalid image size", w, h, c);
        try {
            m_data = new Data{w, h, c, 1, new float[w * h * c]};
        } catch (...) {
            try {
                delete[] m_data->data;
                m_data->data = nullptr;
            } catch (...) {
            }
            try {
                delete m_data;
                m_data = nullptr;
            } catch (...) {
            }
            throw;
        }
    }

    Img(Data *data) : m_data(data) {
        ++data->ref_cnt;
    }

    Img(const Img &rhs) {
        this->m_data = rhs.m_data;
        ++this->m_data->ref_cnt;
    }

    ~Img() {
        try {
            if (--m_data->ref_cnt == 0) {
                delete m_data;
                m_data = nullptr;
            }
        } catch (...) {}
    }

    Img &operator=(const Img &rhs) {
        if (this == &rhs || this->m_data == rhs.m_data) return *this;
        if (--m_data->ref_cnt == 0) delete m_data;
        m_data = rhs.m_data;
        ++m_data->ref_cnt;
        return *this;
    }

    float *ptr() const { if (m_data) return m_data->data; else return nullptr; }

    Data *data() const { return m_data; }

    void save(const char *filename) {
        std::ofstream f(filename, std::ios_base::out);
        for (int i = 0; i < data()->height * data()->width * data()->channels; ++i) {
            f << (data()->data[i]) << std::endl;
        }
        f.close();
    }
};
