package kr.goldenmine.models

import kr.goldenmine.points.Point
import kr.theterroronline.util.physics.Vector3d
import java.awt.Color
import java.util.*

val rectangleValue = listOf(
    Point(0, 1), Point(1, 2), Point(2, 3), Point(3, 0),
    Point(4, 5), Point(5, 6), Point(6, 7), Point(7, 4),
    Point(0, 4), Point(1, 5), Point(2, 6), Point(3, 7)
)


open class Rectangle(
    val x: Double,
    val y: Double,
    val z: Double,
    val xs: Double,
    val ys: Double,
    val zs: Double,
    color: Color = Color.BLACK
) : Figure(getRectangle(x, y, z, xs, ys, zs), rectangleValue, color) {

}

fun getRectangle(
    x: Double,
    y: Double,
    z: Double,
    xs: Double,
    ys: Double,
    zs: Double
): List<Vector3d> {
    val list: MutableList<Vector3d> =
        ArrayList()
    list.add(Vector3d(x, y, z))
    list.add(Vector3d(x + xs, y, z))
    list.add(Vector3d(x + xs, y + ys, z))
    list.add(Vector3d(x, y + ys, z))
    list.add(Vector3d(x, y, z + zs))
    list.add(Vector3d(x + xs, y, z + zs))
    list.add(Vector3d(x + xs, y + ys, z + zs))
    list.add(Vector3d(x, y + ys, z + zs))
    return list
}