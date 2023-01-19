#pragma once

#include <cblas.h>

#include <cmath>
#include <iomanip>

#if defined (__AVX512F__)

#include <immintrin.h>

#elif defined (__ARM_NEON)

#include <arm_neon.h>

#endif  // SIMD instruction set

#include "../util/imgdata.hpp"
#include "cnnlayer.hpp"

using std::endl;
using std::setw;
using std::vector;

enum ConvPadding {
    same,
    full,
    valid,
};

enum ConvActivation {
    Relu,
    Sigmoid,
    Tanh,
    Linear,
};

namespace conv::activation {
    inline float relu(float x) { return x > 0 ? x : 0; }

    inline float sigmoid(float x) { return 1 / (1 + std::exp(-x)); }

    inline float tanh(float x) { return std::tanh(x); }
}  // namespace conv::activation

class Conv3d : public CnnLayer {
    size_t filters;
    size_t kernel_width;
    size_t kernel_height;
    size_t kernel_depth;
    size_t stride_w;
    size_t stride_h;

    ConvPadding padding;  // openblas
    ConvActivation activation;

    const float *const weight;
    bool useBias;
    const float *const bias;

public:
    Conv3d(size_t filters, size_t kernel_w, size_t kernel_h, size_t kernel_d,
           size_t stride_w, size_t stride_h, const float *const weights,
           bool useBias = false, const float *const biases = nullptr,
           ConvPadding padding = valid, ConvActivation activation = Linear)
            : filters(filters), kernel_width(kernel_w), kernel_height(kernel_h), kernel_depth(kernel_d),
              stride_w(stride_w), stride_h(stride_h), padding(padding), activation(activation),
              weight(weights), useBias(useBias), bias(biases) {
        if (!weights || (useBias && !biases))
            throw InvalidArgs("Weights / Biases not provided");
        if (filters <= 0)
            throw InvalidArgs("Filter number below zero", filters);
        if (kernel_w <= 0 || kernel_h <= 0 || kernel_d <= 0)
            throw InvalidArgs("Invalid kernel size", kernel_w, kernel_h, kernel_d);
        if (stride_w <= 0 || stride_h <= 0)
            throw InvalidArgs("Invalid stride size", stride_w, stride_h);
    }

    vector<size_t> optSize(vector<size_t> &in_size) const override {
        if (in_size.size() != 3) throw InvalidArgs("Input size throw have three dimensions, got", in_size.size());
        vector<size_t> opt;
        for (int i = 0; i < 2; ++i) {
            size_t kernelSize, stride;
            if (i == 0) {
                kernelSize = kernel_width;
                stride = stride_w;
            } else {
                kernelSize = kernel_height;
                stride = stride_h;
            }
            switch (padding) {
                case valid:
                    opt.push_back((in_size.begin()[i] - kernelSize) /
                                  stride + 1);
                    break;
                case same:
                    opt.push_back((in_size.begin()[i] - kernelSize +
                                   2 * (kernelSize / 2)) / stride + 1);
                    break;
                case full:
                    opt.push_back((in_size.begin()[i] - kernelSize +
                                   2 * (kernelSize - 1)) / stride + 1);
                    break;
            }
        }
        opt.push_back(filters);
        return opt;
    }

    bool checkValid(vector<size_t> &last_opt) const override {
        return last_opt.size() == 3 && last_opt[2] == kernel_depth;
    }

    Img forward(const Img &img) const override {
//        size_t opt_channels = filters;
//        size_t opt_width, opt_height, o_channel_step;
//
//        size_t i_col = img.data()->width;
//        size_t i_row = img.data()->height;
//        size_t i_channel_step = i_col * i_row;
//        size_t ki_channel_step = kernel_height * kernel_width;
//        size_t ko_channel_step = kernel_depth * ki_channel_step;
//
//        size_t k_bias_r2c = kernel_height / 2;
//        size_t k_bias_c2c = kernel_width / 2;
//
//        switch (padding) {
//            case same: {
//                opt_width = (img.data()->width - kernel_width + 2 * (kernel_width / 2)) / stride_w + 1;
//                opt_height =
//                        (img.data()->height - kernel_height + 2 * (kernel_height / 2)) / stride_h + 1;
//                break;
//            }
//            case full: {
//                opt_width = (img.data()->width - kernel_width + 2 * (kernel_width - 1)) / stride_w + 1;
//                opt_height =
//                        (img.data()->height - kernel_height + 2 * (kernel_height - 1)) / stride_h + 1;
//                break;
//            }
//            case valid: {
//                opt_width = (img.data()->width - kernel_width) / stride_w + 1;
//                opt_height = (img.data()->height - kernel_height) / stride_h + 1;
//                break;
//            }
//        }
//        o_channel_step = opt_height * opt_width;
//
//
//        Img res(opt_width, opt_height, opt_channels);
//        size_t in_channels = img.data()->channels;
//        const float *idata = img.ptr();
//        float *odata = res.ptr();
//
//        if (padding == valid) {
//#pragma omp parallel for
//            for (size_t o_ch = 0; o_ch < opt_channels; ++o_ch) {
//                for (size_t i_ch = 0; i_ch < in_channels; ++i_ch) {
//                    for (size_t o_idx_r = 0; o_idx_r < opt_height; ++o_idx_r) {
//                        for (size_t o_idx_c = 0; o_idx_c < opt_width; ++o_idx_c) {
//                            for (size_t k_idx_r = 0; k_idx_r < kernel_height; ++k_idx_r) {
//#pragma unroll 8
//                                for (size_t k_idx_c = 0; k_idx_c < kernel_width; ++k_idx_c) {
//                                    odata[o_ch * o_channel_step + o_idx_r * opt_width + o_idx_c] +=
//                                            idata[i_ch * i_channel_step
//                                            + (o_idx_r * stride_h + k_idx_r) * i_col
//                                            + o_idx_c * stride_w + k_idx_c] *
//                                            weight[o_ch * ko_channel_step
//                                            + i_ch * ki_channel_step
//                                            + k_idx_r * kernel_width + k_idx_c];
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//#pragma omp parallel for
//            for (size_t o_ch = 0; o_ch < opt_channels; ++o_ch) {
//                for (size_t i_ch = 0; i_ch < in_channels; ++i_ch) {
//                    for (size_t o_idx_r = 0; o_idx_r < opt_height; ++o_idx_r) {
//                        for (size_t o_idx_c = 0; o_idx_c < opt_width; ++o_idx_c) {
//                            for (size_t k_idx_r = 0; k_idx_r < kernel_height; ++k_idx_r) {
//#pragma unroll 8
//                                for (size_t k_idx_c = 0; k_idx_c < kernel_width; ++k_idx_c) {
//                                    if (padding == same && (o_idx_r * stride_h - k_bias_r2c + k_idx_r < 0
//                                    || o_idx_r * stride_h - k_bias_r2c + k_idx_r >= i_row
//                                    || o_idx_c * stride_w - k_bias_c2c + k_idx_c < 0
//                                    || o_idx_c * stride_w - k_bias_c2c + k_idx_c >= i_col) ||
//                                    padding == full && (o_idx_r * stride_h - k_bias_r2c + 1 < 0
//                                    || o_idx_r * stride_h + k_bias_r2c + 1 >= i_row
//                                    || o_idx_c * stride_w - k_bias_c2c + 1 < 0
//                                    || o_idx_c * stride_w + k_bias_c2c + 1 >= i_col))
//                                        continue;
//                                    odata[o_ch * o_channel_step + o_idx_r * opt_width + o_idx_c] +=
//                                            idata[i_ch * i_channel_step
//                                            + (o_idx_r * stride_h - k_bias_r2c + k_idx_r) * i_col
//                                            + o_idx_c * stride_w - k_bias_c2c + k_idx_c] *
//                                            weight[o_ch * ko_channel_step
//                                            + i_ch * ki_channel_step
//                                            + k_idx_r * kernel_width + k_idx_c];
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        size_t opt_channels = filters;
        size_t opt_width, opt_height, o_channel_step;

        size_t i_col = img.data()->width;
        size_t i_row = img.data()->height;
        size_t i_channel_step = i_col * i_row;

        switch (padding) {
            case same: {
                opt_width = (img.data()->width - kernel_width + 2 * (kernel_width / 2)) / stride_w + 1;
                opt_height =
                        (img.data()->height - kernel_height + 2 * (kernel_height / 2)) / stride_h + 1;
                break;
            }
            case full: {
                opt_width = (img.data()->width - kernel_width + 2 * (kernel_width - 1)) / stride_w + 1;
                opt_height =
                        (img.data()->height - kernel_height + 2 * (kernel_height - 1)) / stride_h + 1;
                break;
            }
            case valid: {
                opt_width = (img.data()->width - kernel_width) / stride_w + 1;
                opt_height = (img.data()->height - kernel_height) / stride_h + 1;
                break;
            }
        }
        o_channel_step = opt_height * opt_width;

        size_t i2c_col = kernel_height * kernel_width * kernel_depth;

        Img im2col(opt_width * opt_height, i2c_col, 1);

        float *p_i2c = im2col.ptr();
        const float *idata = img.ptr();

        size_t rst, cst;
        if (padding == valid) {
            rst = kernel_height / 2;
            cst = kernel_width / 2;
        } else if (padding == same) {
            rst = cst = 0;
        } else {
            rst = kernel_height - 1;
            cst = kernel_width - 1;
        }

#pragma omp parallel for
        for (size_t r = -rst; r < i_row + rst; r += stride_h) {
            for (size_t c = -cst; c < i_col + cst; c += stride_w) {
                for (size_t ch = 0; ch < kernel_depth; ++ch) {
                    for (size_t i = 0; i < kernel_height; ++i) {
                        for (size_t j = 0; j < kernel_width; ++j) {
                            if (r + i < 0 || r + i >= i_row
                                || c + j < 0 || c + j >= i_col)
                                p_i2c[((r + rst) / stride_h * opt_width + (c + cst) / stride_w) *
                                      i2c_col + ch * kernel_width * kernel_height +
                                      i * kernel_width + j] = 0.f;
                            else
                                p_i2c[((r + rst) / stride_h * opt_width + (c + cst) / stride_w) *
                                      i2c_col + ch * kernel_width * kernel_height +
                                      i * kernel_width + j] =
                                        idata[ch * i_channel_step + (r + i) * i_col + c + j];
                        }
                    }
                }
            }
        }

        Img res(opt_width, opt_height, opt_channels);
        cblas_sgemm(CblasRowMajor, CblasNoTrans, CblasTrans,
                    opt_channels, opt_width * opt_height,
                    i2c_col, 1, weight, i2c_col, p_i2c,
                    i2c_col, 0, res.ptr(), opt_height * opt_width);
//        matmulABT(weight, im2col, res);
        float *odata = res.ptr();


        if (useBias) {
#if defined (__AVX512F__)
            size_t cst = o_channel_step / 16;
            for (size_t o_ch = 0; o_ch < opt_channels; ++o_ch) {
                float *p_ubias = odata + o_ch * o_channel_step;
                __m512 _bias = _mm512_set1_ps(bias[o_ch]);
                for (size_t k = 0; k < cst; ++k) {
                    _mm512_storeu_ps(p_ubias, _mm512_add_ps(_mm512_loadu_ps(p_ubias), _bias));
                    p_ubias += 16;
                }
#pragma unroll 8
                for (size_t k = o_channel_step - o_channel_step % 16; k < o_channel_step; ++k) {
                    *p_ubias += bias[o_ch];
                    p_ubias++;
                }
            }
#elif defined (__ARM_NEON)
            size_t cst = o_channel_step / 4;
            float *p_ubias = odata;
            for (size_t o_ch = 0; o_ch < opt_channels; ++o_ch) {
                float32x4_t _bias = vdupq_n_f32(bias[o_ch]);
                for (size_t k = 0; k < cst; ++k) {
                    vst1q_f32(p_ubias, vaddq_f32(vld1q_f32(p_ubias), _bias));
                    p_ubias += 4;
                }
#pragma unroll 8
                for (size_t k = 0; k < o_channel_step % 4; ++k)
                    odata[o_ch * o_channel_step + k] += bias[o_ch];
            }
#else
#pragma unroll 8
            for (size_t o_ch = 0; o_ch < opt_channels; ++o_ch) {
                float _bias = bias[o_ch];
                for (size_t ce = 0; ce < o_channel_step; ++ce)
                    odata[o_ch * o_channel_step + ce] += _bias;
            }
#endif
        }

        switch (activation) {
            case Linear:
                break;
            case Relu: {
                size_t sum = opt_width * opt_height * opt_channels;
#if defined (__AVX512F__)
                size_t cst = sum / 16;
                float *p_ubias = odata;
                __m512 zeros = _mm512_setzero_ps();
                for (size_t k = 0; k < cst; ++k) {
                    _mm512_store_ps(p_ubias, _mm512_max_ps(zeros, _mm512_load_ps(p_ubias)));
                    p_ubias += 16;
                }
#pragma unroll 8
                for (size_t k = sum - sum % 16; k < sum; ++k) {
                    if (*p_ubias < 0) *p_ubias = 0.f;
                    p_ubias++;
                }

#elif defined (__ARM_NEON)
                size_t cst = o_channel_step / 4;
                float *p_ubias = odata;
                float32x4_t zeros = vdupq_n_f32(0.f);
                for (size_t k = 0; k < cst; ++k) {
                    vst1q_f32(p_ubias, vmaxq_f32(vld1q_f32(p_ubias), zeros));
                    p_ubias += 4;
                }
#pragma unroll 8
                for (size_t k = o_channel_step - o_channel_step % 4; k < o_channel_step; ++k)
                    if (p_ubias[k] < 0) p_ubias[k] = 0.f;
#else
#pragma unroll 8
                for (size_t i = 0; i < sum; ++i)
                    if (odata[i] < 0) odata[i] = 0;
#endif
                break;
            }
            case Sigmoid: {
                size_t sum = opt_width * opt_height * opt_channels;
#pragma unroll 8
                for (size_t i = 0; i < sum; ++i)
                    if (odata[i] < 0) odata[i] = 1 / (1 + std::exp(-odata[i]));
                break;
            }
            case Tanh: {
                size_t sum = opt_width * opt_height * opt_channels;
#pragma unroll 8
                for (size_t i = 0; i < sum; ++i)
                    if (odata[i] < 0) odata[i] = std::tanh(odata[i]);
                break;
            }
        }
        return res;
    }

    void summary(ostream &os,
                 const vector<size_t> &input) const override {
        if (input.

                size()

            != 3)
            throw InvalidArgs("Input dimension mismatch", input.

                    size()

            );
        os << setw(8) << "Conv3D" << setw(7) << "(";
        for (
                int i = 0;
                i < 3; ++i) {
            os << setw(5) << input[i];
            if (i < 2) os << ", ";
        }
        os << ")  -->  (";
        for (
                int i = 0;
                i < 2; ++i) {
            size_t kernelSize, stride;
            if (i == 0) {
                kernelSize = kernel_width;
                stride = stride_w;
            } else {
                kernelSize = kernel_height;
                stride = stride_h;
            }
            switch (padding) {
                case valid:
                    os << setw(5) << (input[i] - kernelSize) / stride + 1;
                    break;
                case same:
                    os << setw(5)
                       << (input[i] - kernelSize + 2 * (kernelSize / 2)) /
                          stride +
                          1;
                    break;
                case full:
                    os << setw(5)
                       << (input[i] - kernelSize + 2 * (kernelSize - 1)) /
                          stride +
                          1;
                    break;
            }
            os << ", ";
        }
        os << setw(5) << filters << ")" <<
           std::endl;
    }
};
