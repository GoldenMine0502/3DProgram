package kr.goldenmine.models

import kr.theterroronline.util.physics.Vector3d

interface PhysicalObject {
    val currentPos: Vector3d
    val lastPos: Vector3d
    val velocity: Vector3d
    val acceleration: Vector3d

    fun next()
}