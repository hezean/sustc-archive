#pragma once

struct point {
    float x;
    float y;
    point() : x(0), y(0) {}
    point(float x, float y) : x(x), y(y) {}
};

void setPtrs(point src, point& dst1, point& dst2);

point middle(point* p1, point* p2);
