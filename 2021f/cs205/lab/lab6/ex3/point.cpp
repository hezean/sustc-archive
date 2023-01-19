#include "point.h"

void setPtrs(point src, point& dst1, point& dst2) {
    dst1.x = src.x;
    dst1.y = src.y;
    dst2.x = src.x;
    dst2.y = src.y;
}

point middle(point* p1, point* p2) {
    return point((p1->x + p2->x) / 2, (p1->y + p2->y) / 2);
}
