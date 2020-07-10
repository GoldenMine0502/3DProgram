package kr.goldenmine.collision

import kr.theterroronline.util.physics.Vector3d

enum class Face(val skip : Vector3d, val add : Vector3d) {
    Y(Vector3d(0, 0, 0), Vector3d(0, -1, 0)),
    YPlus(Vector3d(0, 1, 0), Vector3d(0, 1, 0)),
    X(Vector3d(0, 0, 0), Vector3d(-1, 0, 0)),
    XPlus(Vector3d(1, 0, 0), Vector3d(1, 0, 0)),
    Z(Vector3d(0, 0, 0), Vector3d(0, 0, -1)),
    ZPlus(Vector3d(0, 0, 1), Vector3d(0, 0, 1));
}