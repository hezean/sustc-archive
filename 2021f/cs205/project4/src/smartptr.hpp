#pragma once

#include "matexcept.hpp"

#define HERE __FILE__, __LINE__
static int cnt;

namespace mat {

template <class T>
class SmartPtr {
    template <class V>
    friend class SmartPtr;

    template <class V>
    friend class Matrix;

    struct MatElem {
        int row;
        int col;
        T *data;
        int ref_cnt;

        ~MatElem() {
            try {
                delete[] data;
                data = nullptr;
            } catch (...) {
            }
        }
    };

    MatElem *m_data;

public:
    SmartPtr(int r, int c) : m_data(nullptr) {
        if (r <= 0 || c <= 0) throw InvalidArgs({r, c});
        try {
            m_data = new MatElem{r, c, new T[r * c], 1};
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

    SmartPtr(const SmartPtr &rhs) {
        this->m_data = rhs.m_data;
        ++this->m_data->ref_cnt;
    }

    template <class V>
    explicit SmartPtr(const SmartPtr<V> &op) {
        try {
            int row = op.m_data->row, col = op.m_data->col;
            this->m_data = new MatElem{row, col, new T[row * col], 1};
            auto thisData = m_data->data;
            auto opData = op.m_data->data;
#pragma unroll 16
            for (int i = 0; i < row * col; ++i)
                thisData[i] = static_cast<T>(opData[i]);
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

    ~SmartPtr() {
        try {
            if (--m_data->ref_cnt == 0) {
                delete m_data;
                m_data = nullptr;
            }
        } catch (...) {
        }
    }

    SmartPtr &operator=(const SmartPtr &rhs) {
        if (this == &rhs || this->m_data == rhs.m_data) return *this;
        if (--m_data->ref_cnt == 0) delete m_data;
        m_data = rhs.m_data;
        ++m_data->ref_cnt;
        return *this;
    }

    T &operator()(int r, int c) const {
        if (r < 0 || c < 0) throw InvalidArgs({r, c});
        if (r > m_data->row) throw MatIndexOutOfBound(HERE, r, m_data->row);
        if (c > m_data->col) throw MatIndexOutOfBound(HERE, c, m_data->col);
        return m_data->data[r * m_data->col + c];
    }
};

}  // namespace mat
