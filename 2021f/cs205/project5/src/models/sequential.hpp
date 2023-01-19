#pragma once

#include <initializer_list>
#include <vector>

#include "../layers/conv3d.hpp"
#include "../layers/imginput.hpp"
#include "../layers/pool3d.hpp"
#include "../layers/fullconn.hpp"

#include "../util/extras.hpp"

using std::initializer_list;
using std::ostream;
using std::vector;

float totalTime = 0;

class Sequential {
    vector<CnnLayer *> m_layers;
    vector<size_t> m_optSize;

public:
    Sequential() = default;

    Sequential(initializer_list<CnnLayer *> layers) {
        if (dynamic_cast<ImgInput *>(*layers.begin()) == nullptr ||
            dynamic_cast<FullConn *>(*(layers.end() - 1)) == nullptr) {
            for (auto &l : layers)
                try {
                    delete l;
                } catch (...) {}
            throw InvalidArgs("The first layer must be ImgInput && the last layer must be FullConn");
        }
        for (auto &layer : layers) {
            /**
             * the first layer <aka. ImgInput> actually doesn't care what inSize is
             * but should only has one in a module
             * since when the first time we check the m_optSize, it is empty
             * we can use this to check whether an ImgInput is added inside the module but not the first layer
             */
            if (!layer->checkValid(m_optSize)) {
                for (auto &l:layers) delete l;
                throw InvalidArgs("Invalid module structure, conflicts on", typeid(layer).name());
            }
            m_optSize = layer->optSize(m_optSize);
        }
        m_layers.insert(m_layers.end(), layers);
    }

    ~Sequential() {
        for (auto &layer : m_layers) {
            try {
                delete layer;
            } catch (...) {
            }
        }
    }

    void add(CnnLayer *layer) {
        if (m_layers.empty() && !dynamic_cast<ImgInput *>(layer)) {
            delete layer;
            throw InvalidArgs("The first layer must be ImgInput");
        }
        if (!layer->checkValid(m_optSize)) {
            delete layer;
            throw InvalidArgs("Module structure is invalid");
        }
        m_layers.push_back(layer);
        m_optSize = layer->optSize(m_optSize);
    }

    vector<float> predict(const char *img, int cvt = -1) {
        if (m_layers.empty() || !dynamic_cast<FullConn *>(*(m_layers.end() - 1)))
            throw CnnException("Module still incomplete");

//        Img res = dynamic_cast<ImgInput *>(m_layers.at(0))->prepare(img, cvt);
//        auto start1 = std::chrono::steady_clock::now();
//        for (int i = 1; i < m_layers.size(); ++i) {
//            res = m_layers.at(i)->forward(res);
//        }

        TIME_START
        Img res = dynamic_cast<ImgInput *>(m_layers.at(0))->prepare(img, cvt);
        TIME_END("Read img")
        auto start1 = std::chrono::steady_clock::now();
        for (int i = 1; i < m_layers.size(); ++i) {
            TIME_START
            res = m_layers.at(i)->forward(res);
            TIME_END(i)
        }

        auto delta1 = static_cast<float>((std::chrono::steady_clock::now() - start1).count()) * unit_;
        totalTime += delta1;
        float *conf = res.ptr();
        vector<float> pred{conf[0], conf[1]};
        return pred;
    }

    friend ostream &operator<<(ostream &os, const Sequential &module) {
        os << "CNN Module <Sequential, " << module.m_layers.size() << " layers>" << endl;
        os << "================================================================" << endl;
        vector<size_t> *_tmp = nullptr;  // inSize is not actually needed for ImgInput
        vector<size_t> optSize = module.m_layers[0]->optSize(*_tmp);
        for (auto &layer : module.m_layers) {
            layer->summary(os, optSize);
            os << "----------------------------------------------------------------" << endl;
            optSize = layer->optSize(optSize);
        }
        return os;
    }

    void summary() {
        cout << (*this);
    }
};
