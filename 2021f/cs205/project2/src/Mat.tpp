template<typename T>
size_t Mat<T>::getCol() const {
    return col_;
}

template<typename T>
size_t Mat<T>::getRow() const {
    return row_;
}

template<typename T>
T **Mat<T>::getData() const {
    return data_;
}

template<typename T>
Mat<T>::Mat(size_t row, size_t col) {
    this->col_ = col;
    this->row_ = row;
    this->data_ = new T *[row];
    for (size_t i = 0; i < row; ++i) {
        this->data_[i] = new T[col];
        for (size_t j = 0; j < col; ++j) this->data_[i][j] = 0;
    }
}

template<typename T>
Mat<T>::Mat(size_t row, size_t col, ifstream &f) {
    TIMER  // it prompts the user how long IO cost: load a Mat from a txt
    this->col_ = col;
    this->row_ = row;
    this->data_ = new T *[row];
    for (size_t i = 0; i < row; ++i) {
        this->data_[i] = new T[col];
        string line;
        std::getline(f, line, '\n');
        std::stringstream ssl(line);
        int col_i = 0;
        while (ssl >> this->data_[i][col_i++]);
    }
    std::cout << "Initialize Mat (" << col << "*" << row << ") finished. ";
    // the timer info will be attached here
    f.clear();
    f.seekg(0L, std::ios_base::beg);
}

template<typename T>
Mat<T>::~Mat() {
    for (size_t i = 0; i < this->row_; ++i) delete[] this->data_[i];
    delete[] this->data_;
}

template<typename T>
Mat<T> Mat<T>::operator+(const Mat<T> &op) {
    if (this->col_ != op.getCol() || this->row_ != op.getRow())
        throw invalid_argument("size not same");
    Mat<T> sum(this->row_, this->col_);
    for (size_t i = 0; i < this->row_; ++i)
        for (size_t j = 0; j < this->col_; ++j)
            sum.getData()[i][j] = this->data_[i][j] + op.getData()[i][j];
    return sum;
}

template<typename T>
Mat<T> Mat<T>::operator-(const Mat<T> &op) {
    if (this->col_ != op.getCol() || this->row_ != op.getRow())
        throw invalid_argument("size not same");
    Mat<T> sum(this->row_, this->col_);
    for (size_t i = 0; i < this->row_; ++i)
        for (size_t j = 0; j < this->col_; ++j)
            sum.getData()[i][j] = this->data_[i][j] - op.getData()[i][j];
    return sum;
}

template<typename T>
Mat<T> Mat<T>::dot_n3(const Mat<T> &m1, const Mat<T> &m2) {
    TIMER
    Mat<T> prod(m1.getCol(), m2.getRow());
    for (size_t i = 0; i < m1.getRow(); ++i) {
        for (size_t j = 0; j < m2.getCol(); ++j) {
            for (size_t k = 0; k < m1.getCol(); k++)
                prod.getData()[i][j] +=
                        (m1.getData()[i][k] * m2.getData()[k][j]);
        }
    }
    cout << ">>> Product calculated: by <dot_n3>. ";
    return prod;
}

template<typename T>
Mat<T> Mat<T>::dot_change_ord(const Mat<T> &m1, const Mat<T> &m2) {
    TIMER
    Mat<T> prod(m1.getRow(), m2.getCol());
    for (size_t i = 0; i < m1.getRow(); ++i) {
        for (size_t k = 0; k < m2.getRow(); ++k) {
            auto op = m1.getData()[i][k];
            for (size_t j = 0; j < m2.getCol(); ++j)
                prod.getData()[i][j] += op * m2.getData()[k][j];
        }
    }
    cout << ">>> Product calculated: by <dot_change_ord>. ";
    return prod;
}

template<typename T>
Mat<T> Mat<T>::dot_strassen(const Mat<T> &m1, const Mat<T> &m2) {
    TIMER
    if (m1.getRow() != m1.getCol() || m2.getRow() != m2.getCol()) {
        cout << "Strassen algo can only handle square matrices" << endl;
        return Mat<T>(0, 0);
    }
    size_t n = m1.getRow();
    if (n & (n - 1)) {  // a method to check whether n is 2^x
        cout << "Matrices' size must be a power of two" << endl;
        return Mat<T>(0, 0);
    }

    cout << ">>> Product calculated: by <dot_strassen>. ";
    return Mat<T>::strassen(m1, m2);
}

template<typename T>
Mat<T> Mat<T>::strassen(const Mat<T> &m1, const Mat<T> &m2) {
    size_t n = m1.getCol();

    if (n <= 256) {
        Mat<T> prod(m1.getRow(), m2.getCol());
        for (size_t i = 0; i < m1.getRow(); ++i) {
            for (size_t k = 0; k < m2.getRow(); ++k) {
                auto op = m1.getData()[i][k];
                for (size_t j = 0; j < m2.getCol(); ++j)
                    prod.getData()[i][j] += op * m2.getData()[k][j];
            }
        }
        return prod;
    }

    n /= 2;
    auto A11 = Mat<T>::sub_mat(m1, 0, 0, n, n);
    auto A12 = Mat<T>::sub_mat(m1, 0, n, n, n);
    auto A21 = Mat<T>::sub_mat(m1, n, 0, n, n);
    auto A22 = Mat<T>::sub_mat(m1, n, n, n, n);

    auto B11 = Mat<T>::sub_mat(m2, 0, 0, n, n);
    auto B12 = Mat<T>::sub_mat(m2, 0, n, n, n);
    auto B21 = Mat<T>::sub_mat(m2, n, 0, n, n);
    auto B22 = Mat<T>::sub_mat(m2, n, n, n, n);

    auto S1 = B12 - B22;
    auto S2 = A11 + A12;
    auto S3 = A21 + A22;
    auto S4 = B21 - B11;
    auto S5 = A11 + A22;
    auto S6 = B11 + B22;
    auto S7 = A12 - A22;
    auto S8 = B21 + B22;
    auto S9 = A11 - A21;
    auto S10 = B11 + B12;

    auto P1 = strassen(A11, S1);
    auto P2 = strassen(S2, B22);
    auto P3 = strassen(S3, B11);
    auto P4 = strassen(A22, S4);
    auto P5 = strassen(S5, S6);
    auto P6 = strassen(S7, S8);
    auto P7 = strassen(S9, S10);

    auto C11 = P5 + P4 - P2 + P6;
    auto C12 = P1 + P2;
    auto C21 = P3 + P4;
    auto C22 = P5 + P1 - P3 - P7;

    return Mat<T>::merge(C11, C12, C21, C22);
}

template<typename T>
Mat<T> Mat<T>::sub_mat(const Mat<T> &m, size_t rowst, size_t colst,
                       size_t rowlen, size_t collen) {
    Mat<T> sub(rowlen, collen);
    for (size_t i = 0; i < rowlen; ++i)
        for (size_t j = 0; j < collen; ++j)
            sub.data_[i][j] = m.getData()[i + rowst][j + colst];
    return sub;
}

template<typename T>
Mat<T> Mat<T>::merge(Mat<T> &m11, Mat<T> &m12, Mat<T> &m21, Mat<T> &m22) {
    Mat<T> mer(m11.getRow() * 2, m11.getCol() * 2);
    for (size_t i = 0; i < m11.getRow(); ++i) {
        for (size_t j = 0; j < m11.getCol(); ++j) {
            mer.getData()[i][j] = m11.getData()[i][j];
            mer.getData()[i][j + m11.getCol()] = m12.getData()[i][j];
            mer.getData()[i + m11.getRow()][j] = m21.getData()[i][j];
            mer.getData()[i + m11.getRow()][j + m11.getCol()] =
                    m22.getData()[i][j];
        }
    }
    return mer;
}

float _mm_vec_dot(const float *v1, const float *v2, const size_t len) {
    float s[8] = {0};
    __m256 a, b;
    __m256 c = _mm256_setzero_ps();

    for (size_t i = 0; i < len - 7; i += 8) {
            a = _mm256_load_ps(v1 + i);
            b = _mm256_load_ps(v2 + i);
            c = _mm256_add_ps(c, _mm256_mul_ps(a, b));
    }
    _mm256_store_ps(s, c);

    auto sum = s[0] + s[1] + s[2] + s[3] + s[4] + s[5] + s[6] + s[7];
    for (size_t i = len - 1; i >= len - len % 8; i--) sum += v1[i] * v2[i];
    return sum;
}

template<>
Mat<float> Mat<float>::_mm_dot_float(const Mat<float> &m1,
                                     const Mat<float> &m2) {
    TIMER
    Mat<float> prod(m1.getRow(), m2.getCol());
    for (size_t i = 0; i < m1.getRow(); ++i)
        for (size_t j = 0; j < m2.getCol(); ++j) {
            auto v1 = static_cast<float *>(std::aligned_alloc(256,
                                                              max(static_cast<int>(m1.getCol() * sizeof(float)), 256)));
            auto v2 = static_cast<float *>(std::aligned_alloc(256,
                                                              max(static_cast<int>(m2.getRow() * sizeof(float)), 256)));
            memcpy(v1, m1.getData()[i], m1.getCol() * sizeof(float));
            for (size_t k = 0; k < m2.getRow(); ++k) v2[k] = m2.getData()[k][j];
            prod.data_[i][j] = _mm_vec_dot(v1, v2, m2.getRow());
            delete[] v2;
        }

    cout << ">>> Product calculated: by <_mm_dot_float>. ";
    return prod;
}

template<typename T>
Mat<T> Mat<T>::dot_blas(const Mat<T> &m1, const Mat<T> &m2) {
    TIMER
    Mat<T> prod(m1.getRow(), m2.getCol());
    for (size_t i = 0; i < m1.getRow(); ++i)
        for (size_t j = 0; j < m2.getCol(); ++j) {
            auto v2 = new T[m2.getRow()];
            for (size_t k = 0; k < m2.getRow(); ++k) v2[k] = m2.getData()[k][j];
            prod.data_[i][j] =
                    cblas_sdot(m2.getRow(), m1.getData()[i], 1, v2, 1);
            delete[] v2;
        }

    cout << ">>> Product calculated: by <dot_blas>. ";
    return prod;
}

static void calc_tar_pos(Mat<float> *tar, const Mat<float> *m1,
                         const Mat<float> *m2, size_t row, size_t col) {
    auto v1 = static_cast<float *>(std::aligned_alloc(256,
                                                      max(static_cast<int>(m1->getCol() * sizeof(float)), 256)));
    auto v2 = static_cast<float *>(std::aligned_alloc(256,
                                                      max(static_cast<int>(m2->getRow() * sizeof(float)), 256)));
    for (size_t k = 0; k < m2->getRow(); ++k) v2[k] = m2->getData()[k][col];
    tar->getData()[row][col] =
            _mm_vec_dot(v1, v2, m1->getCol());
    delete[] v2;
}

template<>
Mat<float> Mat<float>::dot_mul_threads(const Mat<float> &m1,
                                       const Mat<float> &m2) {
    TIMER
    Mat<float> prod(m1.getRow(), m2.getCol());
    // due to the time limit, I reused _mm_vec_dot, thus only support float
    /*
     * rather than using the manual way like the following:
     *      auto *threads = new thread[THREADS];
     *      for (int i = 0; i < THREADS; ++i) ...
     * I use ThreadPool, see: https://github.com/progschj/ThreadPool
     */
    ThreadPool pool(THREADS);
    for (size_t i = 0; i < m1.getRow(); i++)
        for (size_t j = 0; j < m2.getCol(); j++)
            pool.enqueue(calc_tar_pos, &prod, &m1, &m2, i, j);
    cout << ">>> Product calculated: by <dot_mul_threads>. ";
    return prod;
}

template<typename T>
void Mat<T>::show_ln(size_t row) {
    if (this->col_ > 6) {
        for (size_t i = 0; i < 3; ++i) printf("%.2f   ", this->data_[row][i]);
        std::cout << "...   ";
        for (size_t i = this->col_ - 3; i < this->col_; ++i)
            printf("%.2f   ", this->data_[row][i]);
    } else
        for (size_t i = 0; i < this->col_; ++i)
            printf("%.2f   ", this->data_[row][i]);
    std::cout << std::endl;
}

template<typename T>
void Mat<T>::show() {
    if (this->row_ > 6) {
        for (size_t i = 0; i < 3; ++i) show_ln(i);
        std::cout << "..." << std::endl;
        for (size_t i = this->row_ - 3; i < this->row_; ++i) show_ln(i);
    } else
        for (size_t i = 0; i < this->row_; ++i) show_ln(i);
}

template<typename T>
void Mat<T>::save(const string &tar) {
    TIMER
    std::fstream out("./" + tar, fstream::out);
    out.setf(ios::fixed);
    out.precision(3);
    for (size_t i = 0; i < this->row_; ++i) {
        for (size_t j = 0; j < this->col_ - 1; ++j)
            out << this->data_[i][j] << " ";
        out << this->data_[i][this->col_ - 1] << endl;
    }
    out.close();
    std::cout << "Answer saved (tar = " << tar << "). ";
}

template<typename T>
void Mat<T>::rand(size_t row, size_t col, const string &tar) {
    // stl random: https://blog.csdn.net/a897297499/article/details/88633947
    default_random_engine e(time(nullptr));
    uniform_real_distribution<T> u(-100, 100);
    auto rdmat = Mat<T>(row, col);
    for (size_t r = 0; r < row; ++r)
        for (size_t c = 0; c < col; ++c) rdmat.data_[r][c] = u(e);
    rdmat.save(tar);
}

