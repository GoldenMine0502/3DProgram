package kr.goldenmine.models

import kr.theterroronline.util.physics.Vector3d
import java.awt.Color

class SkyRectangle(
    override val velocity: Vector3d = Vector3d(0, 0, 0, true),
    val flying: Double = 0.01,
    x: Double,
    y: Double,
    z: Double,
    xs: Double,
    ys: Double,
    zs: Double,
    color: Color = Color.BLACK
) : Rectangle(x, y, z, xs, ys, zs, color), PhysicalObject {
    override val currentPos = Vector3d(x, y, z, true)
    val size = Vector3d(xs, ys, zs, false)
    override val lastPos: Vector3d = Vector3d(currentPos)
    override val acceleration: Vector3d = Vector3d(0, 0, 0, true)

    override fun calculateNextPosition() {
        if(velocity.x > flying) velocity.x += -flying
        if(velocity.x < -flying) velocity.x += flying
        if(-flying <= velocity.x && velocity.x <= flying) velocity.x = 0.0

        if(velocity.y > flying) velocity.y += -flying
        if(velocity.y < -flying) velocity.y += flying
        if(-flying <= velocity.y && velocity.y <= flying) velocity.y = 0.0

        if(velocity.z > flying) velocity.z += -flying
        if(velocity.z < -flying) velocity.z += flying
        if(-flying <= velocity.z && velocity.z <= flying) velocity.z = 0.0

        velocity += acceleration

        lastPos.copyFrom(currentPos)
        currentPos += velocity

        val newCoordinates = getRectangle(currentPos.x, currentPos.y, currentPos.z, size.x, size.y, size.z)
        for(i in newCoordinates.indices) {
            coordinates[i] = newCoordinates[i]
        }
    }
}