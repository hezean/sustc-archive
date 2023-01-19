#pragma once

#include <cmath>
#include <iomanip>
#include <opencv2/core/core.hpp>
//#include <opencv2/highgui/highgui.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "cnnlayer.hpp"
#include "../util/imgdata.hpp"

using std::endl;
using std::setw;

class ImgInput : public CnnLayer {
public:
    enum ImgResizeType {
        force,
        no_deformation,
        center,
        left_upper,
    };

private:
    ImgResizeType resizeOpt;
    size_t opt_width;
    size_t opt_height;
    size_t opt_channels;

public:
    ImgInput(size_t w, size_t h, size_t c, ImgResizeType res = force)
            : resizeOpt(res), opt_width(w), opt_height(h), opt_channels(c) {
        if (w <= 0 || h <= 0 || c <= 0)
            throw InvalidArgs("(Reshaped) input size must > 0", w, h, c);
    }

    void summary(ostream &os,
                 const vector<size_t> &input) const override {
        os << "ImgInput" << setw(7) << "(" << "    ?,     ?,     ?)  -->  (";
        os << setw(5) << opt_width << ", " << setw(5) << opt_height
           << ", " << setw(5) << opt_channels << ")\n";
    }

    bool checkValid(vector<size_t> &last_opt) const override {
        return last_opt.empty();
    }

    vector<size_t> optSize(vector<size_t> &in_size) const override {
        return vector<size_t>{opt_width, opt_height, opt_channels};
    }

    /**
     * @param cvt -1 -> auto detect: if <in_channels> == <out_channels>, do nothing
     *                               else if <out_channels> == 1, read the file as <gray scale>
     *                               else @throws Fatal(Convert rule undefined)
     *            0 ~ 143 -> force cv::cvtColor, in/out channels mismatch may cause @throws Fatal(...)
     *            otherwise -> @throws InvalidArgs(Convert rule not found)
     * @return Img (SmartPtr in Project 4), pass by value, ref_cnt will be well managed
     */
    Img prepare(const char *file, int cvt = -1) const {
        if (cvt < -1 || cvt > 143) throw InvalidArgs("Convert rule not found", cvt);
        cv::Mat image;
        if (cvt == -1) {
            if (opt_channels == 1) {
                image = cv::imread(file, cv::IMREAD_GRAYSCALE);
                if (image.data == nullptr)
                    throw Fatal("File not found");
            } else {
                image = cv::imread(file, cv::IMREAD_UNCHANGED);
                if (image.data == nullptr)
                    throw Fatal("File not found");
                if (image.channels() != opt_channels)
                    throw Fatal("Convert rule undefined");
            }
        } else {
            cv::Mat tmp = cv::imread(file, cv::IMREAD_UNCHANGED);
            if (tmp.data == nullptr)
                throw Fatal("File not found");
            try {
                cv::cvtColor(tmp, image, cvt);
            } catch (const cv::Exception &e) {
                throw Fatal("Convert failed: convert rule doesn't match input file channel");
            }
            if (image.channels() != opt_channels)
                throw InvalidArgs("Wrong convert rule: output channel mismatch", image.channels(), opt_channels);
        }  // input channel is well-adjusted
        if ((resizeOpt == force || resizeOpt == no_deformation) &&
            (static_cast<int>(opt_width) <= 0 || static_cast<int>(opt_height) <= 0))
            throw Fatal("Scale size too large: OpenCV only supports size in range (0, 2147483647]");
        Img blob_img(opt_width, opt_height, opt_channels);
        resizer(image, blob_img.ptr());

        return blob_img;
    }

    Img forward(const Img &img) const override {
        std::cerr << "Avoid calling <ImgInput.forward> since it does nothing." << std::endl;
        return img;
    }

private:
    void resizer(const cv::Mat &ori, float *adj) const {
        cv::Mat res;
        if (ori.cols != opt_width || ori.rows != opt_height) {
            switch (resizeOpt) {  // TODO: impl <center> & <left_upper>
                case force: {
                    cv::resize(ori, res, cv::Size(static_cast<int>(opt_width),
                                                  static_cast<int>(opt_height)));
                    break;
                }
                case no_deformation: {
                    int rc = opt_height / opt_width, inr, inc;
                    if (ori.rows * rc <= ori.cols) {
                        inr = ori.rows;
                        inc = ori.rows * rc;
                    } else {
                        inr = ori.cols / rc;
                        inc = ori.cols;
                    }
                    cv::Mat black(inc, inr, CV_8UC3, cv::Scalar(0, 0, 0));
                    cv::Mat imageROI;
                    imageROI = black(cv::Rect(std::abs(inc - ori.cols) / 2, std::abs(inr - ori.rows) / 2,
                                              ori.cols, ori.rows));
                    ori.copyTo(imageROI);
                    cv::resize(black, res, cv::Size(static_cast<int>(opt_width),
                                                    static_cast<int>(opt_height)));
                    break;
                }
                default:
                    throw Fatal("Not implemented");
            }
        } else { res = ori; }
        size_t opt_ch_step = opt_width * opt_height;
        for (size_t r = 0; r < opt_height; ++r) {
            uchar *p_rdata = res.ptr<uchar>(r);
            for (size_t c = 0; c < opt_width; ++c) {
#pragma unroll 3
                for (size_t ch = 0; ch < opt_channels; ++ch) {
                    adj[ch * opt_ch_step + r * opt_width + c] =
//                            static_cast<float>(p_rdata[c * opt_channels + ch]) / 255.f;
                            static_cast<float>(p_rdata[c * opt_channels + (opt_channels - ch)]) / 255.f;
                }
            }
        }
//        cv::imshow("preview",res);
    }
};
