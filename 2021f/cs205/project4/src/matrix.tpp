#include "matrix.hpp"

namespace mat {
template <typename T>
Matrix<T>::Matrix(int r, int c, int ch) noexcept(false)
    : m_row(r), m_col(c), m_rbias(0), m_cbias(0) {
    if (r <= 0 || c <= 0 || ch <= 0)
        throw InvalidArgs(std::initializer_list<int>{r, c, ch});
    for (int i = 0; i < ch; ++i) m_data.emplace_back(r, c);
}

template <typename T>
Matrix<T>::Matrix(
    std::initializer_list<std::initializer_list<std::initializer_list<T> > >
        elem3d) noexcept(false) {
    if (elem3d.size() == 0) throw MatException("Empty element list");
    auto src3d = elem3d.begin();
    m_row = src3d[0].size();
    m_col = src3d[0].begin()[0].size();
    m_rbias = m_cbias = 0;
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int c = 0; c < elem3d.size(); ++c) {
        auto src2d = src3d[c].begin();
        if (src3d[c].size() == 0) throw MatException("Empty element list");
        if (src3d[c].size() != m_row)
            throw InvalidArgs("Different channels have different row_num");
        for (int r = 0; r < m_row; ++r) {
            if (src2d[r].size() == 0) throw MatException("Empty element list");
            if (src2d[r].size() != m_col)
                throw InvalidArgs("Different rows have different col_num");
        }
    }
    for (int c = 0; c < elem3d.size(); ++c) m_data.emplace_back(m_row, m_col);
#if not defined(__DISABLE_OMP)
#pragma omp parallel
#pragma omp single nowait
#endif
    for (int c = 0; c < elem3d.size(); ++c) {
#if not defined(__DISABLE_OMP)
#pragma omp task firstprivate(c)
#endif
        {
            auto data = m_data.at(c).m_data->data;
            auto src2d = src3d[c].begin();
            for (int i = 0; i < m_row; ++i) {
                auto src1d = src2d[i].begin();
                for (int j = 0; j < m_col; ++j) data[i * m_col + j] = src1d[j];
            }
        }
    }
}

template <typename T>
Matrix<T>::Matrix(const Matrix<T> &m, int row, int col, int rbias, int cbias) noexcept(false) {
    if (row <= 0 || col <= 0 || rbias < 0 || cbias < 0)
        throw InvalidArgs(std::initializer_list<int>{row, col, rbias, cbias});
    for (auto &ch : m.m_data) {
        if (row + rbias > ch.m_data->row)
            throw MatException(
                "ROI out of bound of the origin matrix: row+rbias > ori_row");
        if (col + cbias > ch.m_data->col)
            throw MatException(
                "ROI out of bound of the origin matrix: col+cbias > ori_col");
    }
    m_row = row;
    m_col = col;
    m_rbias = rbias;
    m_cbias = cbias;
    m_data = m.m_data;
}

template <typename T>
template <class V>
Matrix<T>::Matrix(const Matrix<V> &m) noexcept(false)
    : m_row(m.m_row), m_col(m.m_col), m_rbias(m.m_rbias), m_cbias(m.m_cbias) {
#if not defined(__DISABLE_OMP)
#pragma omp parallel
#pragma omp single nowait
#endif
    for (int i = 0; i < m.m_data.size(); ++i) {
#if not defined(__DISABLE_OMP)
#pragma omp task firstprivate(i)
#endif
        { m_data.emplace_back(m.m_data.at(i)); }
    }
}

template <typename T>
Matrix<T>::Matrix(std::fstream &f) noexcept(false)
    : m_rbias(0), m_cbias(0) {
    if (!f.is_open())
        throw MatException(strerror(errno));
    m_row = file_rows(f);
    m_col = file_cols(f);
    m_data.emplace_back(m_row, m_col);
    auto data = m_data.at(0).m_data->data;
    std::string line;
    std::stringstream ss;
    for (int i = 0; i < m_row; ++i) {
        std::getline(f, line, '\n');
        ss.clear();
        ss << line;
        int col_i = 0;
        while (ss >> data[i * m_col + col_i++]) ;
        if (col_i != m_col + 1)
            throw MatException(
                "Invalid file content: column number isn't fixed");
    }
    f.seekg(0L, std::ios_base::beg);
}

template <typename T>
void Matrix<T>::addChannel(SmartPtr<T> &ch, int where) {
    where %= channels();
    if (where < 0)
        m_data.insert(m_data.end() + 1 + where, ch);
    else
        m_data.insert(m_data.begin() + where, ch);
}

template <typename T>
void Matrix<T>::dropChannel(int which) {
    if (which < 0 || which >= channels())
        throw MatException("Channel index out of bound");
    if (channels() == 1)
        throw MatException(
            "Matrix only has one channel, dropping it will cause MathErr");
    m_data.erase(m_data.begin() + which);
}

template <typename T>
[[nodiscard]] T &Matrix<T>::operator()(int r, int c, int ch) const {
    if (r < 0 || c < 0) throw MatIndexOutOfBound(HERE, (r < 0 ? r : c));
    if (r > m_row) throw MatIndexOutOfBound(HERE, r, m_row);
    if (c > m_col) throw MatIndexOutOfBound(HERE, c, m_col);
    if (ch > m_data.size())
        std::cout << "[warning] matrix has " << m_data.size() << "channel(s): "
                  << "using channel" << ch % m_data.size() << "instead"
                  << std::endl;
    return m_data[ch % m_data.size()](r + m_rbias, c + m_cbias);
}

template <typename T>
[[nodiscard]] Matrix<T> Matrix<T>::clone(const Matrix<T> &tar) {
    int channels = tar.channels();
    Matrix cpy(tar.m_row, tar.m_col, channels);
#if not defined(__DISABLE_OMP)
#pragma omp parallel
#pragma omp single nowait
#endif
    for (int i = 0; i < channels; ++i) {
#if not defined(__DISABLE_OMP)
#pragma omp task firstprivate(i)
#endif
        {
            auto cpyData = cpy.m_data.at(i).m_data->data;
            auto srcData = tar.m_data.at(i).m_data->data;
            int srcCol = tar.m_data.at(i).m_data->col;
            for (int r = 0; r < tar.m_row; ++r) {
                memcpy(cpyData + r * tar.m_col,
                       srcData + (r + tar.m_rbias) * srcCol + tar.m_cbias,
                       sizeof(T) * tar.m_col);
            }
        }
    }
    return cpy;
}

template <typename T>
Matrix<T> Matrix<T>::operator+(const Matrix<T> &op) const {
    TIMER
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, op.m_row, m_col, op.m_col,
                              channels(), op.channels(), '+');
    int ch = channels();
    Matrix sum(m_row, m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto rhsData = op.m_data.at(i).m_data->data;
        auto sumData = sum.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col &&
            m_row == op.m_data.at(i).m_data->row &&
            m_col == op.m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) sumData[i] = lhsData[i] + rhsData[i];
        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    sumData[r * m_col + c] =
                        lhsData[(r + m_rbias) * m_col + m_cbias + c] +
                        rhsData[(r + op.m_rbias) * m_col + op.m_cbias + c];
                }
            }
        }
    }
    return sum;
}

template <typename T>
Matrix<T> &Matrix<T>::operator+=(const Matrix<T> &op) {
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, op.m_row, m_col, op.m_col,
                              channels(), op.channels(), '+');
    int ch = channels();
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto rhsData = op.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col &&
            m_row == op.m_data.at(i).m_data->row &&
            m_col == op.m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) lhsData[i] += rhsData[i];
        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    lhsData[(r + m_rbias) * m_col + m_cbias + c] +=
                        rhsData[(r + op.m_rbias) * m_col + op.m_cbias + c];
                }
            }
        }
    }
    return *this;
}

template <typename T>
template <typename V>
Matrix<T> Matrix<T>::operator+(const V &op) const {
    auto oppo = static_cast<T>(op);
    int ch = channels();
    Matrix sum(m_row, m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto sumData = sum.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) sumData[i] = lhsData[i] + oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    sumData[r * m_col + c] =
                        lhsData[(r + m_rbias) * m_col + m_cbias + c] + oppo;
            }
        }
    }
    return sum;
}

template <typename T>
template <typename V>
Matrix<T> &Matrix<T>::operator+=(const V &op) {
    auto oppo = static_cast<T>(op);
    int ch = channels();
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) lhsData[i] += oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    lhsData[(r + m_rbias) * m_col + m_cbias + c] += oppo;
            }
        }
    }
    return *this;
}

template <typename T>
Matrix<T> Matrix<T>::operator-(const Matrix<T> &op) const {
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, op.m_row, m_col, op.m_col,
                              channels(), op.channels(), '-');
    int ch = channels();
    Matrix sum(m_row, m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto rhsData = op.m_data.at(i).m_data->data;
        auto sumData = sum.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col &&
            m_row == op.m_data.at(i).m_data->row &&
            m_col == op.m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) sumData[i] = lhsData[i] - rhsData[i];
        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    sumData[r * m_col + c] =
                        lhsData[(r + m_rbias) * m_col + m_cbias + c] -
                        rhsData[(r + op.m_rbias) * m_col + op.m_cbias + c];
                }
            }
        }
    }
    return sum;
}

template <typename T>
Matrix<T> &Matrix<T>::operator-=(const Matrix<T> &op) {
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, op.m_row, m_col, op.m_col,
                              channels(), op.channels(), '-');
    int ch = channels();
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto rhsData = op.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col &&
            m_row == op.m_data.at(i).m_data->row &&
            m_col == op.m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) lhsData[i] -= rhsData[i];
        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    lhsData[(r + m_rbias) * m_col + m_cbias + c] -=
                        rhsData[(r + op.m_rbias) * m_col + op.m_cbias + c];
                }
            }
        }
    }
    return *this;
}

template <typename T>
template <typename V>
Matrix<T> Matrix<T>::operator-(const V &op) const {
    auto oppo = static_cast<T>(op);
    int ch = channels();
    Matrix sum(m_row, m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto sumData = sum.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) sumData[i] = lhsData[i] - oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    sumData[r * m_col + c] =
                        lhsData[(r + m_rbias) * m_col + m_cbias + c] - oppo;
            }
        }
    }
    return sum;
}

template <typename T>
template <typename V>
Matrix<T> &Matrix<T>::operator-=(const V &op) {
    auto oppo = static_cast<T>(op);
    int ch = channels();
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) lhsData[i] -= oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    lhsData[(r + m_rbias) * m_col + m_cbias + c] -= oppo;
            }
        }
    }
    return *this;
}

template <typename T>
template <typename V>
Matrix<T> Matrix<T>::operator*(const V &op) const {
    auto oppo = static_cast<T>(op);
    int ch = channels();
    Matrix sum(m_row, m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        auto sumData = sum.m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) sumData[i] = lhsData[i] * oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    sumData[r * m_col + c] =
                        lhsData[(r + m_rbias) * m_col + m_cbias + c] * oppo;
            }
        }
    }
    return sum;
}

template <typename T>
Matrix<T> Matrix<T>::operator*(const Matrix<T> &op) const {
    TIMER
    if (m_col != op.m_row || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, m_col, channels(), op.m_row,
                              op.m_col, op.channels(), '*');
    int ch = channels();
    Matrix prod(m_row, op.m_col, ch);
    prod.fill(static_cast<T>(0));
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int c = 0; c < ch; ++c) {
        auto lhsData = m_data.at(c).m_data->data;
        auto rhsData = op.m_data.at(c).m_data->data;
        auto prodData = prod.m_data.at(c).m_data->data;
        int lo_col = m_data.at(c).m_data->col;
        int ro_col = op.m_data.at(c).m_data->col;
#if not defined(__DISABLE_OMP)
#pragma omp parallel for num_threads(7)
#endif
        for (int i = 0; i < m_row; ++i) {
            for (int k = 0; k < op.m_row; ++k) {
                const T &lelem = lhsData[(i + m_rbias) * lo_col + k + m_cbias];
#pragma unroll 16
                for (int j = 0; j < ro_col; ++j) {
                    prodData[i * m_col + j] +=
                        lelem *
                        rhsData[(k + op.m_rbias) * ro_col + j + op.m_cbias];
                }
            }
        }
    }
    return prod;
}

template <typename T>
Matrix<T> &Matrix<T>::operator*=(const Matrix<T> &op) {
    (*this) = (*this) * op;
    return *this;
}

template <typename T>
template <typename V>
Matrix<T> &Matrix<T>::operator*=(const V &op) {
    auto oppo = static_cast<T>(op);
    int ch = channels();
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto lhsData = m_data.at(i).m_data->data;
        if (m_row == m_data.at(i).m_data->row &&
            m_col == m_data.at(i).m_data->col) {
            int cnt = m_row * m_col;
#pragma unroll 16
            for (int i = 0; i < cnt; i++) lhsData[i] *= oppo;

        } else {
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c)
                    lhsData[(r + m_rbias) * m_col + m_cbias + c] *= oppo;
            }
        }
    }
    return *this;
}

template <class T>
std::ostream &operator<<(std::ostream &out, const Matrix<T> &mat) {
    out << "Mat<" << typeid(T).name() << "> (" << mat.m_row << "*" << mat.m_col
        << "*" << mat.m_data.size() << ")" << std::endl;
    for (int i = 0; i < mat.m_data.size(); ++i) {
        out << "[[ Channel " << i << " ]]" << std::endl;
        out << "[";
        for (int j = 0; j < mat.m_row; ++j) {
            if (j == 3 && mat.m_row > 6) {
                out << "..." << std::endl;
                j = mat.m_row - 4;
                continue;
            }
            if (j != 0) out << " ";
            out << "[";
            for (int k = 0; k < mat.m_col; ++k) {
                if (k == 3 && mat.m_col > 6) {
                    out << "  ...  ";
                    k = mat.m_col - 4;
                    continue;
                }
                out << std::setw(6) << mat(j, k, i);
                if (k < mat.m_col - 1) out << "  ";
            }
            out << "]";
            if (j != mat.m_row - 1) out << "," << std::endl;
        }
        out << "]" << std::endl;
    }
    return out;
}

template <class T>
std::fstream &operator<<(std::fstream &out, const Matrix<T> &mat) {
    out << "Mat<" << typeid(T).name() << "> (" << mat.m_row << "*" << mat.m_col
        << "*" << mat.m_data.size() << ")" << std::endl;
    for (int i = 0; i < mat.m_data.size(); i++) {
        out << "[[ Channel " << i << " ]]" << std::endl;
        for (int j = 0; j < mat.m_row; j++) {
            for (int k = 0; k < mat.m_col; k++) {
                out << std::setw(6) << mat(j, k, i);
                if (k < mat.m_col - 1) out << "  ";
            }
            out << std::endl;
        }
    }
    return out;
}

template <class T, typename V>
Matrix<T> operator*(const V &lhs, const Matrix<T> &rhs) {
    return rhs * lhs;
}

template <typename T>
bool Matrix<T>::operator==(const Matrix &op) const {
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        return false;
    int ch = channels();
    for (int i = 0; i < ch; ++i) {
        if (m_data.at(i).m_data == op.m_data.at(i).m_data) return true;
        for (int j = 0; j < m_row; ++j) {
#pragma unroll 16
            for (int k = 0; k < m_row; ++k)
                if (m_data.at(i)(j, k) != op.m_data.at(i)(j, k)) return false;
        }
    }
    return true;
}

template <typename T>
bool Matrix<T>::elemEquals(const Matrix<T> &op,
                           std::function<bool(T &, T &)> const &eq) const {
    if (m_row != op.m_row || m_col != op.m_col || channels() != op.channels())
        return false;
    int ch = channels();
    for (int i = 0; i < ch; ++i) {
        if (m_data.at(i).m_data == op.m_data.at(i).m_data) return true;
        for (int j = 0; j < m_row; ++j) {
#pragma unroll 16
            for (int k = 0; k < m_row; ++k)
                if (!eq(m_data.at(i)(j, k), op.m_data.at(i)(j, k)))
                    return false;
        }
    }
    return true;
}

template <typename T>
[[nodiscard]] Matrix<T> Matrix<T>::merge(std::initializer_list<T> weights) {
    int ch = m_data.size();
    if (weights.size() != ch)
        throw InvalidArgs("Weights provided don't match channel number");
    Matrix res(this->m_row, this->m_col);
    res.fill(static_cast<T>(0));
    auto resData = res.m_data.at(0).m_data->data;
    for (int i = 0; i < ch; ++i) {
        auto oriData = m_data.at(i).m_data->data;
        int oriCol = m_data.at(i).m_data->col;
        T &w = weights.begin()[i];
        for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
            for (int c = 0; c < m_col; ++c) {
                resData[r * m_col + c] +=
                    oriData[(r + m_rbias) * oriCol + c + m_cbias] * w;
            }
        }
    }
    return res;
}

template <typename T>
[[nodiscard]] Matrix<T> Matrix<T>::transpose() const {
    int ch = channels();
    Matrix tsp(m_col, m_row, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int i = 0; i < ch; ++i) {
        auto srcData = m_data.at(i).m_data->data;
        auto tspData = tsp.m_data.at(i).m_data->data;
        int ori_col = m_data.at(i).m_data->col;
        for (int j = 0; j < m_row; ++j) {
            for (int k = 0; k < m_col; ++k) {
                tspData[k * m_row + j] =
                    srcData[(j + m_rbias) * ori_col + k + m_cbias];
            }
        }
    }
    return tsp;
}

template <typename T>
void Matrix<T>::fill(const T &elem) {
    int channels = m_data.size();
#if not defined(__DISABLE_OMP)
#pragma omp parallel
#pragma omp single nowait
#endif
    for (int i = 0; i < channels; ++i) {
#if not defined(__DISABLE_OMP)
#pragma omp task firstprivate(i)
#endif
        {
            auto data = m_data.at(i).m_data->data;
            int srcCol = m_data.at(i).m_data->col;
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    data[(r + m_rbias) * srcCol + c + m_cbias] = elem;
                }
            }
        }
    }
}

template <typename T>
void Matrix<T>::fillRand() {
    int channels = m_data.size();
#if not defined(__DISABLE_OMP)
#pragma omp parallel
#pragma omp single nowait
#endif
    for (int i = 0; i < channels; ++i) {
#if not defined(__DISABLE_OMP)
#pragma omp task firstprivate(i)
#endif
        {
            auto data = m_data.at(i).m_data->data;
            int srcCol = m_data.at(i).m_data->col;
            for (int r = 0; r < m_row; ++r) {
#pragma unroll 16
                for (int c = 0; c < m_col; ++c) {
                    if (TYPE_IS(T, float) || TYPE_IS(T, double))
                        data[(r + m_rbias) * srcCol + c + m_cbias] =
                            static_cast<T>(
                                (static_cast<double>(rand() % 5000) / 160));
                    else
                        data[(r + m_rbias) * srcCol + c + m_cbias] =
                            static_cast<T>(rand() % 5000);
                }
            }
        }
    }
}

template <typename T>
[[nodiscard]] Matrix<T> Matrix<T>::randMat(int r, int c, int ch) {
    Matrix res(r, c, ch);
    res.fillRand();
    return res;
}

#if not defined(__DISABLE_SIMD)

template <>
MatUC8 MatUC8::operator*(const MatUC8 &op) const {
    TIMER
    if (m_col != op.m_row || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, m_col, channels(), op.m_row,
                              op.m_col, op.channels(), '*');
    int ch = channels();
    Matrix rhs = op.transpose();
    Matrix prod(m_row, op.m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    Timer st;
    for (int c = 0; c < ch; ++c) {
        auto lhsData = m_data.at(c).m_data->data;
        auto rhsData = rhs.m_data.at(c).m_data->data;
        auto prodData = prod.m_data.at(c).m_data->data;
        int lo_col = m_data.at(c).m_data->col;
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
#if defined(__AVX512F__)
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                __m512i res = _mm512_setzero_epi32();
                for (int k = 0; k < m_col / 64; k += 64) {
                    res = _mm512_add_epi8(
                        res,
                        _mm512_mul_epi8(
                            _mm512_loadu_epi8(lhsData + (i + m_rbias) * lo_col +
                                              k * 64 + m_cbias),
                            _mm512_loadu_epi8(rhsData + j * rhs.m_col +
                                              k * 64)));
                }
                prodData[i * op.m_col + j] = _mm512_reduce_add_epi32(res);
                for (int k = m_col - (m_col % 64); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#elif defined(__AVX2__)
        float temp[8];
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                __m256i res = _mm256_set1_epi8(0);
                for (int k = 0; k < m_col / 32; ++k) {
                    res = _mm256_add_epi8(
                        _mm256_mul_epi8(
                            _mm256_loadu_epi8(lhsData + (i + m_rbias) * lo_col +
                                              k * 32 + m_cbias),
                            _mm256_loadu_epi8(rhsData + j * rhs.m_col +
                                              k * 32)),
                        res);
                }
                _mm256_store_ps(temp, res);
                prodData[i * op.m_col + j] = temp[0] + temp[1] + temp[2] +
                                             temp[3] + temp[4] + temp[5] +
                                             temp[6] + temp[7];
                for (int k = m_col - (m_col % 32); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#elif defined(__ARM_NEON)
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                uint8x16_t res = vdupq_n_u8(0);
                for (int k = 0; k < m_col / 16; ++k) {
                    res = vmlaq_u8(res,
                                   vld1q_u8(lhsData + (i + m_rbias) * lo_col +
                                            k * 4 + m_cbias),
                                   vld1q_u8(rhsData + j * rhs.m_col + k * 4));
                }
                prodData[i * op.m_col + j] = vaddvq_u8(res);
                for (int k = m_col - (m_col % 4); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#else
        throw MatException("SIMD not supported, check the g++/gcc args");
#endif
    }
    return prod;
}

template <>
MatF32 MatF32::operator*(const MatF32 &op) const {
    TIMER
    if (m_col != op.m_row || channels() != op.channels())
        throw MatSizeMismatch(HERE, m_row, m_col, channels(), op.m_row,
                              op.m_col, op.channels(), '*');
    int ch = channels();
    Matrix rhs = op.transpose();
    Matrix prod(m_row, op.m_col, ch);
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
    for (int c = 0; c < ch; ++c) {
        auto lhsData = m_data.at(c).m_data->data;
        auto rhsData = rhs.m_data.at(c).m_data->data;
        auto prodData = prod.m_data.at(c).m_data->data;
        int lo_col = m_data.at(c).m_data->col;
#if not defined(__DISABLE_OMP)
#pragma omp parallel for
#endif
#if defined(__AVX512F__)
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                __m512 res = _mm512_setzero_ps();
                for (int k = 0; k < m_col / 16; ++k) {
                    res = _mm512_fmadd_ps(
                        _mm512_loadu_ps(lhsData + (i + m_rbias) * lo_col +
                                        k * 16 + m_cbias),
                        _mm512_loadu_ps(rhsData + j * rhs.m_col + k * 16), res);
                }
                prodData[i * op.m_col + j] = _mm512_reduce_add_ps(res);
                for (int k = m_col - (m_col % 16); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#elif defined(__AVX2__)
        float temp[8];
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                __m256 res = _mm256_setzero_ps();
                for (int k = 0; k < m_col / 8; ++k) {
                    res = _mm256_fmadd_ps(
                        _mm256_loadu_ps(lhsData + (i + m_rbias) * lo_col +
                                        k * 8 + m_cbias),
                        _mm256_loadu_ps(rhsData + j * rhs.m_col + k * 8), res);
                }
                _mm256_store_ps(temp, res);
                prodData[i * op.m_col + j] = temp[0] + temp[1] + temp[2] +
                                             temp[3] + temp[4] + temp[5] +
                                             temp[6] + temp[7];
                for (int k = m_col - (m_col % 8); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#elif defined(__ARM_NEON)
        for (int i = 0; i < m_row; ++i) {
            for (int j = 0; j < op.m_col; ++j) {
                float32x4_t res = vdupq_n_f32(.0f);
                for (int k = 0; k < m_col / 4; ++k) {
                    res = vmlaq_f32(res,
                                    vld1q_f32(lhsData + (i + m_rbias) * lo_col +
                                              k * 4 + m_cbias),
                                    vld1q_f32(rhsData + j * rhs.m_col + k * 4));
                }
                prodData[i * op.m_col + j] = vaddvq_f32(res);
                for (int k = m_col - (m_col % 4); k < m_col; ++k) {
                    prodData[i * op.m_col + j] +=
                        lhsData[(i + op.m_rbias) * lo_col + k + m_cbias] *
                        rhsData[j * rhs.m_col + k];
                }
            }
        }
#else
        throw MatException("SIMD not supported, check the g++/gcc args");
#endif
    }
    return prod;
}

#endif

}  // namespace mat
