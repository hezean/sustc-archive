#pragma once

enum PoolRule {
    MaxPooling,
    AvgPooling,
};

class Pooling3D : public CnnLayer {
    size_t kernel_h;
    size_t kernel_w;
    size_t kernel_c;

    size_t stride_h;
    size_t stride_w;
    size_t stride_c;

    PoolRule rule;

public:
    Pooling3D(size_t kh, size_t kw, size_t kc, size_t sh, size_t sw, size_t sc, PoolRule pr)
            : kernel_h(kh), kernel_w(kw), kernel_c(kc), stride_h(sh), stride_w(sw), stride_c(sc), rule(pr) {
        if (kh <= 0 || kw <= 0 || kc <= 0
            || sh < kh || sw < kw || sc < kc)
            throw InvalidArgs("Invalid pooling kernel / stride", kh, kw, kc, sh, sw, sc);
    }

    Img forward(const Img &img) const override {
        size_t opt_width = (img.data()->width - kernel_w) / stride_w + 1;
        size_t opt_height = (img.data()->height - kernel_h) / stride_h + 1;
        size_t opt_channels = (img.data()->channels - kernel_c) / stride_c + 1;
        size_t o_channel_step = opt_height * opt_width;

        size_t i_col = img.data()->width;
        size_t i_channel_step = img.data()->width * img.data()->height;

        Img res(opt_width, opt_height, opt_channels);

        const float *idata = img.ptr();
        float *odata = res.ptr();

        switch (rule) {
            case MaxPooling: {
                for (size_t o_idx_d = 0; o_idx_d < opt_channels; ++o_idx_d) {
                    for (size_t o_idx_r = 0; o_idx_r < opt_height; ++o_idx_r) {
                        for (size_t o_idx_c = 0; o_idx_c < opt_width; ++o_idx_c) {
                            float tmax = idata[o_idx_d * stride_c * i_channel_step
                                               + o_idx_r * stride_h * i_col
                                               + o_idx_c * stride_w];
                            for (int i = 0; i < kernel_c; ++i) {
                                for (int j = 0; j < kernel_w; ++j) {
                                    for (int k = 0; k < kernel_h; ++k) {
                                        tmax = std::max(tmax, idata[(o_idx_d + i) * stride_c * i_channel_step
                                                                    + (o_idx_r * stride_h + j) * i_col
                                                                    + o_idx_c * stride_w + k]);
                                    }
                                }
                            }
                            odata[o_idx_d * o_channel_step + o_idx_r * opt_width + o_idx_c] = tmax;
                        }
                    }
                }
                break;
            }
            case AvgPooling: {
                for (size_t o_idx_d = 0; o_idx_d < opt_channels; ++o_idx_d) {
                    for (size_t o_idx_r = 0; o_idx_r < opt_height; ++o_idx_r) {
                        for (size_t o_idx_c = 0; o_idx_c < opt_width; ++o_idx_c) {
                            float sum = 0.f;
                            for (int i = 0; i < kernel_c; ++i) {
                                for (int j = 0; j < kernel_w; ++j) {
                                    for (int k = 0; k < kernel_h; ++k) {
                                        sum += idata[(o_idx_d + i) * stride_c * i_channel_step
                                                     + (o_idx_r * stride_h + j) * i_col
                                                     + o_idx_c * stride_w + k];
                                    }
                                }
                            }
                            odata[o_idx_d * o_channel_step + o_idx_r * opt_width + o_idx_c] =
                                    sum / (kernel_h * kernel_c * kernel_w);
                        }
                    }
                }
                break;
            }
        }
        return res;
    }

    void summary(ostream &os,
                 const vector<size_t> &input) const override {
        if (input.size() != 3)
            throw InvalidArgs("Input dimension mismatch", input.size());
        os << setw(8) << (rule == MaxPooling ? "MaxPool" : "AvgPool")
           << setw(7) << "(";
        for (int i = 0; i < 3; ++i) {
            os << setw(5) << input[i];
            if (i < 2) os << ", ";
        }
        os << ")  -->  (";
        for (int i = 0; i < 3; ++i) {
            size_t kernelSize, stride;
            if (i == 0) {
                kernelSize = kernel_w;
                stride = stride_w;
            } else if (i == 1) {
                kernelSize = kernel_h;
                stride = stride_h;
            } else {
                kernelSize = kernel_c;
                stride = stride_c;
            }
            os << setw(5)
               << (input[i] - kernelSize) / stride + 1;
            os << (i == 2 ? ")\n" : ", ");
        }
    }

    bool checkValid(vector<size_t> &last_opt) const override {
        return (last_opt.size() == 3 &&
                last_opt[0] >= kernel_h &&
                last_opt[1] >= kernel_w &&
                last_opt[2] >= kernel_c);
    };

    vector<size_t> optSize(vector<size_t> &in_size) const override {
        size_t opt_width = (in_size[0] - kernel_w) / stride_w + 1;
        size_t opt_height = (in_size[1] - kernel_h) / stride_h + 1;
        size_t opt_channels = (in_size[2] - kernel_c) / stride_c + 1;
        return vector<size_t>{opt_width, opt_height, opt_channels};
    }
};
