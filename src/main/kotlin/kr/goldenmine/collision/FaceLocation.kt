package kr.theterroronline.util.physics

import kr.goldenmine.collision.CollideResult

data class FaceLocation(val face: Face, val pos: Vector3d, val intersect: Vector3d) {
    fun toCollideResult(result: Int, meta: Any?): CollideResult {
        return CollideResult(intersect, face, result, meta)
    }
}