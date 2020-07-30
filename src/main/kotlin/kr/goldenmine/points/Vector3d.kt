package kr.theterroronline.util.physics

import kr.goldenmine.graphic.util.Face
import kr.goldenmine.models.Dot
import kr.goldenmine.models.SkyRectangle
import kr.goldenmine.points.Point
//import org.bukkit.Location
//import org.bukkit.World
//import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

val xVec = Vector3d(1.0, 0.0, 0.0)
val yVec = Vector3d(0.0, 1.0, 0.0)
val zVec = Vector3d(0.0, 0.0, 1.0)

val xVecN = Vector3d(-1.0, 0.0, 0.0)
val yVecN = Vector3d(0.0, -1.0, 0.0)
val zVecN = Vector3d(0.0, 0.0, -1.0)

val faceArr = Face.values()

fun floor(num: Double): Int {
    val floor = num.toInt()
    return if (floor.toDouble() == num) floor else floor - java.lang.Double.doubleToRawLongBits(num).ushr(63).toInt()
}

/**
 * faceDirection: 평면의 법선벡터
 * facePos: 평면을 구성하기 위한 평면의 어떠한 점 중 하나
 * lineDirection: 직선의 기울기
 * linePos: 직선을 구성하기 위한 직선의 어떠한 점 중 하나
 */
fun collide(faceDirection: Vector3d, facePos: Vector3d, lineDirection: Vector3d, linePos: Vector3d): Vector3d {
//    val gt = line.dot(faceN) //line.x * faceN.x + line.y * faceN.y + line.z * faceN.z
//    val v = linePos.dot(faceN) - facePos.dot(faceN) //linePos.x * faceN.x + linePos.y * faceN.y + linePos.z * faceN.z - (facePos.x * faceN.x + facePos.y * faceN.y + facePos.z * faceN.z)
//    val t = -v / gt
    val t = -(linePos.dot(faceDirection) - facePos.dot(faceDirection)) / lineDirection.dot(faceDirection)

    return lineDirection * t + linePos //return Vector3d(t * lineDirection.x + linePos.x, t * lineDirection.y + linePos.y, t * lineDirection.z + linePos.z)
}

//fun collide(faceDirection: Vector3d, facePos: Vector3d, lineDirection: Vector3d, linePos: Vector3d): Vector3d {
//    val gt = lineDirection.x * faceDirection.x + lineDirection.y * faceDirection.y + lineDirection.z * faceDirection.z
//    val v = linePos.x * faceDirection.x + linePos.y * faceDirection.y + linePos.z * faceDirection.z -
//            (facePos.x * faceDirection.x + facePos.y * faceDirection.y + facePos.z * faceDirection.z)
//
//    val t = -v / gt
//
//    return Vector3d(t * lineDirection.x + linePos.x, t * lineDirection.y + linePos.y, t * lineDirection.z + linePos.z)
//}

//fun collideInBlock(result: Vector3d, face: Face, pos: Vector3d): Boolean {
//    val currentPos = pos.add(face.skip)
//    if (currentPos.x <= result.x && result.x <= currentPos.x + 1) {
//        if (currentPos.y <= result.y && result.y <= currentPos.y + 1) {
//            if (currentPos.z <= result.z && result.z <= currentPos.z + 1) {
//                return true
//            }
//        }
//    }
//
//    return false
//}
//
//fun collides(
//    pos: Vector3d,
//    startPos: Vector3d,
//    finishPos: Vector3d,
//    xCheck: Int = 0,
//    yCheck: Int = 0,
//    zCheck: Int = 0
//): Pair<FaceLocation, FaceLocation>? {
//    val direction = finishPos.subtract(startPos)
//
//    //println("check: $xCheck $yCheck $zCheck")
//    //println("direction: $direction")
//
//
//    val originalVec = Vector3d(pos)
//    val plusVec = pos.add(Vector3d(1, 1, 1))
//
//    val y = collide(yVecN, originalVec, direction, startPos)
//    val y2 = collide(yVec, plusVec, direction, startPos)
//
//    if (yCheck < 0) {
//        if (collideInBlock(y, Face.Y, pos)) {
//            return Pair(FaceLocation(Face.Y, pos, y), FaceLocation(Face.YPlus, pos, y2))
//        }
//    } else if (yCheck > 0) {
//        if (collideInBlock(y2, Face.YPlus, pos))
//            return Pair(FaceLocation(Face.YPlus, pos, y2), FaceLocation(Face.Y, pos, y))
//    }
//
//    val x = collide(xVecN, originalVec, direction, startPos)
//    val x2 = collide(xVec, plusVec, direction, startPos)
//
//    if (xCheck < 0) {
//        if (collideInBlock(x, Face.X, pos))
//            return Pair(FaceLocation(Face.X, pos, x), FaceLocation(Face.XPlus, pos, x2))
//    } else if (xCheck > 0) {
//        if (collideInBlock(x2, Face.XPlus, pos))
//            return Pair(FaceLocation(Face.XPlus, pos, x2), FaceLocation(Face.X, pos, x))
//    }
//
//    val z = collide(zVecN, originalVec, direction, startPos)
//    val z2 = collide(zVec, plusVec, direction, startPos)
//
//    if (zCheck < 0) {
//        if (collideInBlock(z, Face.Z, pos))
//            return Pair(FaceLocation(Face.Z, pos, z), FaceLocation(Face.ZPlus, pos, z2))
//    } else if (zCheck > 0) {
//        if (collideInBlock(z2, Face.ZPlus, pos))
//            return Pair(FaceLocation(Face.ZPlus, pos, z2), FaceLocation(Face.Z, pos, z))
//    }
//
//    return null
//}

fun getWeight(rectangle: SkyRectangle): Double {
    return rectangle.size.x * rectangle.size.y * rectangle.size.z
}

const val ADAPT_WEIGHT = 0.1

fun applyNewVelocity(dot: Dot, rectangle: SkyRectangle) {
    val ballWeight = dot.radius * dot.radius * dot.radius * Math.PI * 4 / 3 * ADAPT_WEIGHT
    val rectangleWeight = getWeight(rectangle)
    val e = 0.7

    /*
     공의 무게는 1로 가정.
     직육면체의 무게는 cbrt(가로 * 세로 * 높이)로 가정.
     충돌 계수는 0.7로 가정.
     */
//    val practice: Vector3d = dot.velocity.multiply(ballWeight).add(rectangle.velocity.multiply(rectangleWeight))
    // 충돌 전 초기 운동량
    val practice = dot.velocity * ballWeight + rectangle.velocity * rectangleWeight

//    val ballVelocityAfterCollide: Vector3d =
//        practice.subtract(rectangle.velocity.subtract(dot.velocity).multiply(rectangleWeight * e))
//            .divide(ballWeight + rectangleWeight)
    // 충돌 후 공의 속도
    val ballVelocityAfterCollide = (practice - (rectangle.velocity - dot.velocity) * rectangleWeight * e) / (ballWeight + rectangleWeight)
//    val rectangleVelocityAfterCollide =
//        practice.subtract(ballVelocityAfterCollide.multiply(ballWeight)).divide(rectangleWeight)
    // 충돌 후 직육면체의 속도
    val rectangleVelocityAfterCollide = (practice - ballVelocityAfterCollide * ballWeight) / rectangleWeight

    dot.velocity.copyFrom(ballVelocityAfterCollide)
    rectangle.velocity.copyFrom(rectangleVelocityAfterCollide)
}

fun applyNewVelocity(rectangle: SkyRectangle, rectangle2: SkyRectangle) {
    val rectangleWeight = getWeight(rectangle) * ADAPT_WEIGHT
    val rectangle2Weight = getWeight(rectangle2) * ADAPT_WEIGHT
    val e = 0.4
    /*
     공의 무게는 1로 가정.
     직육면체의 무게는 cbrt(가로 * 세로 * 높이)로 가정.
     충돌 계수는 0.4로 가정.
     */
//    val practice: Vector3d = dot.velocity.multiply(ballWeight).add(rectangle.velocity.multiply(rectangleWeight))
    // 충돌 전 초기 운동량
//    val practice = rectangle.velocity * rectangleWeight + rectangle2.velocity * rectangle2Weight
    /*
        e = (v1' - v2') / (v2 - v1)
        m1 * v1 + m2 * v2 = m1 * v1' + m2 * v2'
        충돌계수가 0.9이므로
        e = (v1' - v2') / (v2 - v1)
        e * (v2 - v1) + v1' = v2'
        m1 * v1 + m2 * v2 = m1 * v1' + m2 * (e * (v2 - v1) + v1') = k
        k = m1 * v1' + m2 * 0.9 * (v2 - v1) + m2 * v1'
        (k - m2 * e * (v2 - v1)) / (m1 + m2) = v1' = 충돌 후 공의 속도
        v1' = ((m1 * v1 + m2 * v2 - m2 * e * (v2 - v1)) / (m1 + m2)
        (k - m1 * v1') / m2 = v2'
    */
//    val ballVelocityAfterCollide: Vector3d =
//        practice.subtract(rectangle.velocity.subtract(dot.velocity).multiply(rectangleWeight * e))
//            .divide(ballWeight + rectangleWeight)
    // 충돌 후 공의 속도
//    val rectangleVelocityAfterCollide = (practice - (rectangle2.velocity - rectangle.velocity) * rectangle2Weight * e) / (rectangleWeight + rectangle2Weight)
//    val rectangleVelocityAfterCollide =
//        practice.subtract(ballVelocityAfterCollide.multiply(ballWeight)).divide(rectangleWeight)
    // 충돌 후 직육면체의 속도
//    val rectangle2VelocityAfterCollide = (practice - rectangleVelocityAfterCollide * rectangleWeight) / rectangle2Weight

//    val rectangleVelocityAfterCollide = rectangle.velocity - rectangle2Weight * (1 + e) / (rectangleWeight + rectangle2Weight) * (rectangle.velocity - rectangle2.velocity)
//    val rectangle2VelocityAfterCollide = rectangle.velocity + rectangleWeight * (1 + e) / (rectangleWeight + rectangle2Weight) * (rectangle.velocity - rectangle2.velocity)

    val lastRectangleVelocity = rectangle.velocity.clone()
    val lastRectangle2Velocity = rectangle2.velocity.clone()


    rectangle.velocity -= rectangle2Weight * (1 + e) / (rectangleWeight + rectangle2Weight) * (lastRectangleVelocity - lastRectangle2Velocity)
    rectangle2.velocity += rectangleWeight * (1 + e) / (rectangleWeight + rectangle2Weight) * (lastRectangleVelocity - lastRectangle2Velocity)
    // 해당하는 속도로 설정
//    rectangle.velocity.copyFrom(rectangleVelocityAfterCollide)
//    rectangle2.velocity.copyFrom(rectangle2VelocityAfterCollide)
}
// 공기저항 관련 코드
/*
==공기저항계수==
구: 0.47
정육면체: 1.06

표준 상태에서의 공기 밀도: 1.293


 */
//fun applyDragForce(dot: Dot) {
//    val dotReferenceArea = dot.radius * 2 * Math.PI
//    val densityOfAir = 1.293
//    val velocity = dot.velocity
//    val dragCoefficient = 0.47 // 구의 항력계수
//    val normalizedVelocity = velocity.normalize()
//
//    val dragForce = normalizedVelocity * dragCoefficient * dotReferenceArea * densityOfAir / 2.0
//    println(dragForce)
//    //dot.velocity.copyFrom(dragForce)
//    dot.velocity -= dragForce
//}

fun applyDragForce(dot: Dot) {
    val c = 1.06
    val size = dot.velocity.mag() * c

    if(dot.velocity.getLength() > 0) {
        val drag = -dot.velocity.normalize()

        val result = drag * size
        dot.velocity += result
    }
}

class Vector3d(x: Double, y: Double, z: Double, val editable: Boolean) : Cloneable {


    constructor(x: Double, y: Double, z: Double) : this(x, y, z, false)
    constructor(x: Int, y: Int, z: Int) : this(x, y, z, false)


    var x: Double = x
        set(value) {
            if (editable) {
                field = value
            } else {
                throw UnsupportedOperationException("cannot modify original settings")
            }
        }
    var y: Double = y
        set(value) {
            if (editable) {
                field = value
            } else {
                throw UnsupportedOperationException("cannot modify original settings")
            }
        }
    var z: Double = z
        set(value) {
            if (editable) {
                field = value
            } else {
                throw UnsupportedOperationException("cannot modify original settings")
            }
        }


    val blockX: Int
        get() = floor(x)
    val blockY: Int
        get() = floor(y)
    val blockZ: Int
        get() = floor(z)

    constructor(x: Int, y: Int, z: Int, editable: Boolean) : this(x.toDouble(), y.toDouble(), z.toDouble(), editable)
    constructor(vec: Vector3d, editable: Boolean) : this(vec.x, vec.y, vec.z, editable)
    constructor(vec: Vector3d) : this(vec, vec.editable)

    fun getDistance(vec: Vector3d) =
        Math.sqrt((vec.x - x) * (vec.x - x) + (vec.y - y) * (vec.y - y) + (vec.z - z) * (vec.z - z))

    fun min(vec: Vector3d) = Vector3d(Math.min(x, vec.x), Math.min(y, vec.y), Math.min(z, vec.z))

    fun max(vec: Vector3d) = Vector3d(Math.max(x, vec.x), Math.max(y, vec.y), Math.max(z, vec.z))

    fun setThis(x: Double, y: Double, z: Double): Vector3d {
        this.x = x
        this.y = y
        this.z = z

        return this
    }

    fun setThis(vec: Vector3d): Vector3d = setThis(vec.x, vec.y, vec.z)

    fun add(value: Double) = Vector3d(x + value, y + value, z + value)
    fun addThis(value: Double): Vector3d {
        if (editable) {
            x += value
            y += value
            z += value

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    fun add(vec: Vector3d): Vector3d = add(vec.x, vec.y, vec.z)
    fun add(x: Double, y: Double, z: Double): Vector3d = Vector3d(this.x + x, this.y + y, this.z + z)
    fun addThis(vec: Vector3d) = addThis(vec.x, vec.y, vec.z)
    fun addThis(x: Double, y: Double, z: Double): Vector3d {
        if (editable) {
            this.x += x
            this.y += y
            this.z += z

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    operator fun plusAssign(vec: Vector3d) {
        addThis(vec)
    }


    fun subtract(value: Double) = Vector3d(x - value, y - value, z - value)
    fun subtractThis(value: Double): Vector3d {
        if (editable) {
            x -= value
            y -= value
            z -= value

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    fun subtract(vec: Vector3d): Vector3d = subtract(vec.x, vec.y, vec.z)
    fun subtract(x: Double, y: Double, z: Double): Vector3d = Vector3d(this.x - x, this.y - y, this.z - z)
    fun subtractThis(vec: Vector3d) = subtractThis(vec.x, vec.y, vec.z)
    fun subtractThis(x: Double, y: Double, z: Double): Vector3d {
        if (editable) {
            this.x -= x
            this.y -= y
            this.z -= z
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }

        return this
    }

    operator fun minusAssign(vec: Vector3d) {
        subtractThis(vec)
    }


    fun multiply(value: Double) = Vector3d(x * value, y * value, z * value)
    fun multiplyThis(value: Double): Vector3d {
        if (editable) {
            x *= value
            y *= value
            z *= value

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    fun multiply(vec: Vector3d): Vector3d = multiply(vec.x, vec.y, vec.z)
    fun multiply(x: Double, y: Double, z: Double): Vector3d = Vector3d(this.x * x, this.y * y, this.z * z)
    fun multiplyThis(vec: Vector3d) = multiplyThis(vec.x, vec.y, vec.z)
    fun multiplyThis(x: Double, y: Double, z: Double): Vector3d {
        if (editable) {
            this.x *= x
            this.y *= y
            this.z *= z
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }

        return this
    }

    operator fun timesAssign(vec: Vector3d) {
        multiplyThis(vec)
    }

    fun divide(value: Double) = Vector3d(x / value, y / value, z / value)
    fun divideThis(value: Double): Vector3d {
        if (editable) {
            x /= value
            y /= value
            z /= value

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    fun divide(vec: Vector3d): Vector3d = divide(vec.x, vec.y, vec.z)
    fun divide(x: Double, y: Double, z: Double): Vector3d = Vector3d(this.x / x, this.y / y, this.z / z)
    fun divideThis(vec: Vector3d) = divideThis(vec.x, vec.y, vec.z)
    fun divideThis(x: Double, y: Double, z: Double): Vector3d {
        if (editable) {
            this.x /= x
            this.y /= y
            this.z /= z

            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    operator fun divAssign(vec: Vector3d) {
        divideThis(vec)
    }

    fun dot(other: Vector3d): Double = x * other.x + y * other.y + z * other.z
    fun normalize(): Vector3d {
        val distance = sqrt(dot(this))

        if (distance > 0) {
            return Vector3d(x / distance, y / distance, z / distance)
        } else {
            throw RuntimeException("distance is 0")
        }
    }

    fun normalizeThis(): Vector3d {
        if (editable) {
            val distance = sqrt(dot(this))
            if (distance > 0) {
                x /= distance
                y /= distance
                z /= distance
            } else {
                throw RuntimeException("distance is 0")
            }
            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    public override fun clone(): Vector3d = super.clone() as Vector3d

    fun isPositiveX() = x > 0
    fun isPositiveY() = y > 0
    fun isPositiveZ() = z > 0
    fun isNegativeX() = x < 0
    fun isNegativeY() = y < 0
    fun isNegativeZ() = z < 0
    fun isZeroX() = x == 0.0
    fun isZeroY() = y == 0.0
    fun isZeroZ() = z == 0.0

    override fun toString(): String {
        return "{ (x: $x, y: $y, z: $z), $editable }"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Vector3d) (x == other.x && y == other.y && z == other.z) else false
    }

    operator fun plus(start: Vector3d): Vector3d = add(start)
    operator fun minus(start: Vector3d): Vector3d = subtract(start)
    operator fun times(start: Vector3d): Vector3d = multiply(start)
    operator fun div(start: Vector3d): Vector3d = divide(start)
    operator fun plus(d: Double): Vector3d = add(d)
    operator fun minus(d: Double): Vector3d = subtract(d)
    operator fun times(d: Double): Vector3d = multiply(d)
    operator fun div(d: Double): Vector3d = divide(d)
    fun copyFrom(pos: Vector3d) {
        if (editable) {
            x = pos.x
            y = pos.y
            z = pos.z
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    fun getLength(): Double = sqrt(x * x + y * y + z * z)
    fun mag() = getLength()

    fun get2DPoint(eye: Double, mul: Double): Point {
        val x = x / (z + eye) * eye * mul
        val y = y / (z + eye) * eye * mul
        return Point(x, y)
    }

    fun out(p3d: Vector3d): Vector3d {
        return Vector3d(y * p3d.z - z * p3d.y, z * p3d.y - x * p3d.z, x * p3d.y - y * p3d.x)
    }

    fun getRotatePoint(yaw: Double, pitch: Double): Vector3d {
        // x1 = b * cos(pitch) - a * sin(pitch)
        // y1 = b * sin(pitch) + a * cos(pitch)
        val a = x
        val b = y
        val c = z
        //double z = this.z;
        val d = Point.degree(pitch)
        val d2 = Point.degree(yaw)
        val x1 = b * cos(d) - a * sin(d)
        val y1 = b * sin(d) + a * cos(d)
        val y2 = c * cos(d2) - y1 * sin(d2)
        val z2 = c * sin(d2) + y1 * cos(d2)
        return Vector3d(x1, y2, z2)
    }

    fun swapYZ(): Vector3d {
        if (editable) {
            val temp = y
            y = z
            z = temp
            return this
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }

    // 자신이 다른 벡터에 비해 모든 x, y, z 엘리먼트가 크다 -> -1
    // 자신이 다른 벡터에 비해 모든 x, y, z 엘리먼트가 작다 -> 1
    // 나머지 경우 -> 에러
//    override operator fun compareTo(other: Vector3d): Int {
//        return when {
//            isBiggerThan(other) -> -1
//            isSmallerThan(other) -> 1
//            equals(other) -> 0
//            else -> (x - other.x + y - other.y + z - other.z).toInt()
//        }
//    }

    fun isBiggerThan(other: Vector3d): Boolean {
        return x > other.x && y > other.y && z > other.z
    }

    fun isSmallerThan(other: Vector3d): Boolean {
        return x < other.x && y < other.y && z < other.z
    }

    fun between(smaller: Vector3d, bigger: Vector3d, EPSILON: Double): Boolean {
        val small = Vector3d(Math.min(smaller.x, bigger.x) - EPSILON, Math.min(smaller.y, bigger.y) - EPSILON, Math.min(smaller.z, bigger.z) - EPSILON)
        val big = Vector3d(Math.max(smaller.x, bigger.x) + EPSILON, Math.max(smaller.y, bigger.y) + EPSILON, Math.max(smaller.z, bigger.z) + EPSILON)
        return (isBiggerThan(small) && isSmallerThan(big)) || equals(small) || equals(big)
    }

    operator fun unaryMinus(): Vector3d {
        return Vector3d(-x, -y, -z)
    }

    operator fun timesAssign(d: Double) {
        if(editable) {
            x *= d
            y *= d
            z *= d
        } else {
            throw UnsupportedOperationException("cannot modify original settings")
        }
    }
}

operator fun Number.plus(other: Vector3d): Vector3d {
    return other + toDouble()
}

operator fun Number.minus(other: Vector3d): Vector3d {
    val num = toDouble()
    return Vector3d(num - other.x, num - other.y, num - other.z, other.editable)
}

operator fun Number.times(other: Vector3d): Vector3d {
    return other * toDouble()
}

operator fun Number.div(other: Vector3d): Vector3d {
    val num = toDouble()
    return Vector3d(num / other.x, num / other.y, num / other.z, other.editable)
}

fun getDirection(yaw: Double, pitch: Double, editable: Boolean, multiply: Double = 1.0): Vector3d {
    val xz = cos(Math.toRadians(pitch))

    return Vector3d(
        -xz * sin(Math.toRadians(yaw)) * multiply,
        sin(Math.toRadians(pitch)) * multiply,
        xz * cos(Math.toRadians(yaw)) * multiply,
        editable
    )
}