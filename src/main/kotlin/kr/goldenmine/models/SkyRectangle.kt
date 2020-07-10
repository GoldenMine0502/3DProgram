package kr.goldenmine.models

import kr.theterroronline.util.physics.Vector3d

class SkyRectangle(
    override val currentPos: Vector3d,
    override val velocity: Vector3d,
    val weight: Double = 0.1,
    x: Double,
    y: Double,
    z: Double,
    xs: Double,
    ys: Double,
    zs: Double
) : Rectangle(x, y, z, xs, ys, zs), PhysicalObject {
    override val lastPos: Vector3d = Vector3d(currentPos)
    override val acceleration: Vector3d = Vector3d(0, 0, 0, true)

    override fun next() {
        acceleration.x -= if(acceleration.x > 0) weight else -weight
        acceleration.y -= if(acceleration.y > 0) weight else -weight
        acceleration.z -= if(acceleration.z > 0) weight else -weight

        velocity += acceleration

        lastPos.copyFrom(currentPos)
        currentPos += velocity
    }
}