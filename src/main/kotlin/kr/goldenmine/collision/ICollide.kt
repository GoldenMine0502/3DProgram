package kr.goldenmine.collision

import kr.goldenmine.PointStorage
import kr.theterroronline.util.physics.Vector3d

interface ICollide {
    fun collide(pointStorage: PointStorage, last: Vector3d, current: Vector3d): CollideResult?
}
