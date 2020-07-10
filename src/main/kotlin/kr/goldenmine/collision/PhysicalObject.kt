package kr.goldenmine.collision

import kr.theterroronline.util.physics.Vector3d

class PhysicalObject(val currentPos: Vector3d, val velocity: Vector3d, private val gravity: Double) {
    val lastPos: Vector3d = Vector3d(currentPos)
    val acceleration = Vector3d(0, 0, 0, true)

    fun next() {
        acceleration.y -= gravity
        velocity += acceleration

        lastPos.copyFrom(currentPos)
        currentPos.addThis(velocity.x, velocity.y, velocity.z)
    }
}