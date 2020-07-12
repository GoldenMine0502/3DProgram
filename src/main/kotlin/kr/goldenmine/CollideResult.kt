package kr.goldenmine

import kr.goldenmine.graphic.util.Face
import kr.theterroronline.util.physics.Vector3d

data class CollideResult(val intersect: Vector3d, val face: Face)