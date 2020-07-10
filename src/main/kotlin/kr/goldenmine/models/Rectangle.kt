package kr.goldenmine.models

import kr.goldenmine.points.Point
import kr.goldenmine.points.Point3D
import java.awt.Color
import java.util.*

val rectangleValue = listOf(Point(1, 2), Point(2, 3), Point(3, 4), Point(4, 1),
    Point(5, 6), Point(6, 7), Point(7, 8) ,Point(8, 5),
    Point(1, 5), Point(2, 6), Point(3, 7), Point(4, 8))


open class Rectangle(
    private val x: Double,
    private val y: Double,
    private val z: Double,
    private val xs: Double,
    private val ys: Double,
    private val zs: Double,
    color: Color = Color.BLACK
): Figure(getRectangle(x, y, z, xs, ys, zs), rectangleValue, color) {

}

fun getRectangle(
    x: Double,
    y: Double,
    z: Double,
    xs: Double,
    ys: Double,
    zs: Double
): List<Point3D>? {
    val list: MutableList<Point3D> =
        ArrayList()
    list.add(Point3D(x, y, z))
    list.add(Point3D(x + xs, y, z))
    list.add(Point3D(x + xs, y + ys, z))
    list.add(Point3D(x, y + ys, z))
    list.add(Point3D(x, y, z + zs))
    list.add(Point3D(x + xs, y, z + zs))
    list.add(Point3D(x + xs, y + ys, z + zs))
    list.add(Point3D(x, y + ys, z + zs))
    return list
}