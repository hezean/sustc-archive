#pragma once

#include <cstring>
#include <functional>
#include <iostream>
#include <string>
#include <vector>

#include "matexcept.hpp"
#include "smartptr.hpp"
#include "util.hpp"

#if not defined(__DISABLE_SIMD)

#if defined(__AVX2__) or defined(__AVX512F__)

#include <immintrin.h>

inline __m256i _mm256_mul_epi8(__m256i a, __m256i b) {
    __m256i dst_even = _mm256_mullo_epi16(a, b);
    __m256i dst_odd =
        _mm256_mullo_epi16(_mm256_srli_epi16(a, 8), _mm256_srli_epi16(b, 8));
    return _mm256_or_si256(_mm256_slli_epi16(dst_odd, 8),
                           _mm256_and_si256(dst_even, _mm256_set1_epi16(0xFF)));
}

#if defined(__AVX512F__)
inline __m512i _mm512_mul_epi8(__m512i a, __m512i b) {
    __m512i dst_even = _mm512_mullo_epi16(a, b);
    __m512i dst_odd =
        _mm512_mullo_epi16(_mm512_srli_epi16(a, 8), _mm512_srli_epi16(b, 8));
    return _mm512_or_si512(_mm512_slli_epi16(dst_odd, 8),
                           _mm512_and_si512(dst_even, _mm512_set1_epi16(0xFF)));
}
#endif

#elif defined(__ARM_NEON)

#include <arm_neon.h>

#endif  // SIMD instruction set

#endif  // __DISABLE_SIMD

#define TYPE_IS(to, tar) \
    std::is_same<typename std::decay<to>::type, tar>::type::value

namespace mat {
template <class T>
class Matrix {
    template <class V>
    friend class Matrix;

private:
    int m_row;
    int m_col;
    int m_rbias;
    int m_cbias;
    std::vector<SmartPtr<T>> m_data;

public:
    Matrix(int r, int c, int ch = 1) noexcept(false);

    Matrix(
        std::initializer_list<std::initializer_list<std::initializer_list<T>>>
            elem3d) noexcept(false);

    Matrix(const Matrix<T> &m) = default;

    Matrix(const Matrix<T> &m, int row, int col, int rbias = 0,
           int cbias = 0) noexcept(false);

    template <class V>
    explicit Matrix(const Matrix<V> &m) noexcept(false);

    explicit Matrix(std::fstream &f) noexcept(false);

    ~Matrix() noexcept = default;

    void addChannel(SmartPtr<T> &ch, int where = -1);

    void dropChannel(int which);

    T *getOriData(int ch = 0) { return m_data.at(0).m_data->data; }

    [[nodiscard]] int rows() const { return m_row; }

    [[nodiscard]] int cols() const { return m_col; }

    [[nodiscard]] int channels() const { return m_data.size(); }

    [[nodiscard]] T &operator()(int r, int c, int ch = 1) const;

    [[nodiscard]] static Matrix clone(const Matrix<T> &tar);

    Matrix<T> &operator=(const Matrix<T> &op) = default;

    Matrix operator+(const Matrix &op) const;

    Matrix &operator+=(const Matrix &op);

    template <typename V>
    Matrix operator+(const V &op) const;

    template <typename V>
    Matrix &operator+=(const V &op);

    template <typename V>
    friend Matrix<T> operator+(const V &lhs, const Matrix<T> &rhs) {
        return rhs + lhs;
    }

    Matrix operator-(const Matrix &op) const;

    Matrix &operator-=(const Matrix &op);

    template <typename V>
    Matrix operator-(const V &op) const;

    template <typename V>
    Matrix &operator-=(const V &op);

    template <typename V>
    friend Matrix<T> operator-(const V &lhs, const Matrix<T> &rhs) {
        return -1 * rhs + lhs;
    }

    template <typename V>
    Matrix operator*(const V &op) const;

    template <typename V>
    Matrix &operator*=(const V &op);

    Matrix operator*(const Matrix &op) const;

    Matrix &operator*=(const Matrix &op);

    template <typename V>
    friend Matrix<T> operator*(const V &lhs, const Matrix<T> &rhs) {
        return rhs * lhs;
    }

    bool operator==(const Matrix &op) const;

    bool elemEquals(
        const Matrix &op, std::function<bool(T &, T &)> const &eq =
                              [](T &e1, T &e2) { return e1 == e2; }) const;

    template <typename V>
    bool operator==(const Matrix<V> &op) const {
        return false;
    }

    [[nodiscard]] Matrix merge(std::initializer_list<T> weights);

    [[nodiscard]] Matrix transpose() const;

    void fill(const T &elem);

    void fillRand();

    [[nodiscard]] static Matrix randMat(int r, int c, int ch = 1);

    template <class V>
    friend std::ostream &operator<<(std::ostream &out, const Matrix<V> &mat);

    template <class V>
    friend std::fstream &operator<<(std::fstream &out, const Matrix<V> &mat);
};

typedef Matrix<unsigned char> MatUC8;
typedef Matrix<short> MatI16;
typedef Matrix<int> MatI32;
typedef Matrix<float> MatF32;
typedef Matrix<double> MatF64;
}  // namespace mat

#include "matrix.tpp"
