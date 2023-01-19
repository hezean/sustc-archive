#pragma once

#include <cmath>
#include <vector>

#if defined(__AVX512F__)

#include <immintrin.h>

#elif defined(__ARM_NEON)

#include <arm_neon.h>

#endif  // SIMD instruction set

#if defined(__ENABLE_BLAS)

#include <cblas.h>

#endif

#include "cnnlayer.hpp"

using std::vector;

enum Normalization {
    SoftMax,
};

class FullConn : public CnnLayer {
    size_t inFeatures;
    size_t outFeatures;
    const float *const weights;
    bool useBias;
    const float *const biases;

    Normalization norm;

public:
    FullConn(size_t inFeatures, size_t outFeatures, const float *const weights, bool useBias = false,
             const float *const biases = nullptr, Normalization norm = Normalization::SoftMax)
            : inFeatures(inFeatures), outFeatures(outFeatures), weights(weights), useBias(useBias), biases(biases),
              norm(norm) {
        if (inFeatures <= 0 || outFeatures <= 0) throw InvalidArgs("Invalid feature size", inFeatures, outFeatures);
        if (useBias && !biases) throw InvalidArgs("Biases not provided");
    }

    Img forward(const Img &img) const override {
        float *pimg = img.ptr();
        Img res(outFeatures, 1, 1);
        float *pres = res.ptr();
#if not defined(__DISABLE_BLAS)
        if (useBias) {
            for (size_t i = 0; i < outFeatures; ++i)
                pres[i] = cblas_sdsdot(inFeatures, biases[i], pimg, 1, weights + i * inFeatures, 1);
        } else {
            for (size_t i = 0; i < outFeatures; ++i)
                pres[i] = cblas_sdot(inFeatures, pimg, 1, weights + i * inFeatures, 1);
        }
#elif defined (__AVX512F__)
        size_t ifsimd = inFeatures / 16;
        for (size_t i = 0; i < outFeatures; ++i) {
            float tmp = 0.f;
            for (size_t j = 0; j < ifsimd; ++j) {
                tmp += _mm512_reduce_add_ps(_mm512_mul_ps(_mm512_loadu_ps(weights + i * inFeatures + j * 16),
                                                          _mm512_loadu_ps(pimg + j * 16)));
            }
            for (size_t j = inFeatures - ifsimd * 16; j < inFeatures; ++j) tmp += weights[i * inFeatures + j] * pimg[j];
            pres[i] = tmp;
            if (useBias) pres[i] += biases[i];
        }
#elif defined (__ARM_NEON)  // omitted to save time
#else
        for (size_t i = 0; i < outFeatures; ++i) {
            pres[i] = 0;
#pragma unroll 8
            for (size_t j = 0; j < inFeatures; ++j) pres[i] += pimg[j] * weights[i * inFeatures + j];
            if(useBias) pres[i] += biases[i];
        }
#endif

        switch (norm) {
            case SoftMax: {
                float sum = 0.f;
#pragma unroll 8
                for (size_t i = 0; i < outFeatures; ++i) {
                    sum += exp(pres[i]);
                }
#pragma unroll 8
                for (size_t i = 0; i < outFeatures; ++i) {
                    pres[i] = exp(pres[i]) / sum;
                }
                break;
            }
        }
        return res;
    }

    vector<size_t> optSize(vector<size_t> &in_size) const override {
        return vector<size_t>{outFeatures};
    }

    bool checkValid(vector<size_t> &last_opt)
    const override {
        if (last_opt.empty())
            return false;
        size_t inFea = 1;
        for (size_t i:last_opt)
            inFea *= i;
        return inFea == inFeatures;
    }

    void summary(ostream &os, const vector<size_t> &input) const override {
        os << "FullConn" << setw(7) << "(";
        for (int i = 0; i < input.size(); ++i) {
            os << setw(5) << input[i];
            if (i < input.size() - 1)
                os << ", ";
        }
        os << ")  -->  (" << setw(5) << outFeatures << ")\n";
    }
};
