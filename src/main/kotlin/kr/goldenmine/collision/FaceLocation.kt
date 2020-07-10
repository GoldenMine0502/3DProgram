package kr.theterroronline.util.physics

import kr.goldenmine.collision.CollideResult
import kr.goldenmine.collision.Face

data class FaceLocation(val face: Face, val pos: Vector3d, val intersect: Vector3d) {

}