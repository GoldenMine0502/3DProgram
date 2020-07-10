package kr.goldenmine.models

import kr.goldenmine.points.Point
import kr.theterroronline.util.physics.Vector3d
import java.awt.Color

val defaultLine = listOf(Point(0, 1))

class Line(private val startPoint: Vector3d, private val finishPoint: Vector3d, color: Color): Figure(listOf(startPoint, finishPoint), defaultLine, color) {

}