package kr.goldenmine.models

import kr.theterroronline.util.physics.Vector3d

class Dot(override val currentPos: Vector3d, override val velocity: Vector3d, private val gravity: Double): Figure(listOf()), PhysicalObject {
    override val lastPos: Vector3d = Vector3d(currentPos)
    override val acceleration = Vector3d(0, 0, 0, true)

    override fun next() {
        acceleration.y -= gravity
        velocity += acceleration

        lastPos.copyFrom(currentPos)
        currentPos.addThis(velocity.x, velocity.y, velocity.z)
    }
}