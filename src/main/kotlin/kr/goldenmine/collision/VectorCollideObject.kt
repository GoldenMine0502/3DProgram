package kr.theterroronline.util.physics

//import kr.theterroronline.util.physics.collides.getChunks
//import kr.theterroronline.util.physics.entities.createEntityInfo
//import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis


const val EPSILON = 0.0000001

fun createRectangleObject(x: Double, y: Double, z: Double, xPosPlus: Double, yPosPlus: Double, zPosPlus: Double, epsilon: Double = EPSILON) = VectorCollideObject(
        listOf(
            Vector3d(0, 1, 0),
                Vector3d(0, 1, 0),
                Vector3d(1, 0, 0),
                Vector3d(1, 0, 0),
                Vector3d(0, 0, 1),
                Vector3d(0, 0, 1)
        ), // 방향
        listOf(
                Vector3d(-x/2 + xPosPlus, -y/2 + yPosPlus, -z/2 + zPosPlus),
                Vector3d( x/2 + xPosPlus , y/2 + yPosPlus,  z/2 + zPosPlus),
                Vector3d(-x/2 + xPosPlus, -y/2 + yPosPlus, -z/2 + zPosPlus),
                Vector3d( x/2 + xPosPlus,  y/2 + yPosPlus,  z/2 + zPosPlus),
                Vector3d(-x/2 + xPosPlus, -y/2 + yPosPlus, -z/2 + zPosPlus),
                Vector3d( x/2 + xPosPlus,  y/2 + yPosPlus,  z/2 + zPosPlus)
        ), // 교점
        listOf(
                Pair(
                        Vector3d( -x/2 + xPosPlus,            -y/2 + yPosPlus - epsilon, -z/2 + zPosPlus           ),
                        Vector3d(  x/2 + xPosPlus,            -y/2 + yPosPlus + epsilon,  z/2 + zPosPlus           )
                ), // downY

                Pair(
                        Vector3d( -x/2 + xPosPlus,             y/2 + yPosPlus - epsilon, -z/2 + zPosPlus           ),
                        Vector3d(  x/2 + xPosPlus,             y/2 + yPosPlus + epsilon,  z/2 + zPosPlus           )
                ), // upY

                Pair(
                        Vector3d( -x/2 + xPosPlus - epsilon,  -y/2 + yPosPlus,           -z/2 + zPosPlus           ),
                        Vector3d( -x/2 + xPosPlus + epsilon,   y/2 + yPosPlus,            z/2 + zPosPlus           )
                ),   // downX
                Pair(
                        Vector3d(  x/2 + xPosPlus - epsilon,  -y/2 + yPosPlus,           -z/2 + zPosPlus           ),
                        Vector3d(  x/2 + xPosPlus + epsilon,   y/2 + yPosPlus,            z/2 + zPosPlus           )
                ),   // upX
                Pair(
                        Vector3d( -x/2 + xPosPlus,            -y/2 + yPosPlus,           -z/2 + zPosPlus - epsilon),
                        Vector3d(  x/2 + xPosPlus,             y/2 + yPosPlus,           -z/2 + zPosPlus + epsilon)
                ),// downZ
                Pair(
                        Vector3d( -x/2 + xPosPlus,            -y/2 + yPosPlus,            z/2 + zPosPlus - epsilon),
                        Vector3d(  x/2 + xPosPlus,             y/2 + yPosPlus,            z/2 + zPosPlus + epsilon)
                )// upZ
        )
)

//val m = CraftWorld::class.java.getDeclaredField("world")




//fun main(args : Array<String>) {
//    val last = Vector3d(-50, -50, -50)
//    val current = Vector3d(50, 50, 50)
//    val pos = Vector3d(2, 2, 2)
//    val vector = createEntityInfo(14, 14, true)
//    printTime {
//        for(i in 0..10_000_000) {
//            val exception = HashSet<Int>()
//
//            exception.add(if (current.y - last.y > 0) 1 else 0)
//            exception.add(if (current.x - last.x > 0) 3 else 2)
//            exception.add(if (current.z - last.z > 0) 5 else 4)
//
//            vector.collides(last, current, pos, exception)
//        }
//    }
//
////    printTime {
////        for(i in 0..1_000_000) {
////            getChunks(last, current)
////        }
////    }
//
//    printTime {
//        val list = ArrayList<Pair<Int, Int>>()
//        for(i in 0..10_000_000) {
//            var startX = last.blockX shr 4
//            var startZ = last.blockZ shr 4
//            var finishX = current.blockX shr 4
//            var finishZ = current.blockX shr 4
//
//            for(x in startX .. finishX step 16) {
//                for(z in startZ .. finishZ step 16) {
//                    list.add(Pair(x, z))
//                }
//            }
//        }
//    }
//}
//
//fun printTime(lambda: () -> (Unit)) {
//    println("Completed in ${ measureTimeMillis(lambda) }ms")
//}

class VectorCollideObject(private val vecList: List<Vector3d>, private val positionList: List<Vector3d>, private val collideRangeList: List<Pair<Vector3d, Vector3d>>) {

    val minimumPos: Vector3d
    val maximumPos: Vector3d

    val minimumAndMaxmimumPosCached = ArrayList<Vector3d>()

    init {
        val minimum = Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, true)
        val maximum = Vector3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, true)

        collideRangeList.forEach {
            minimum.x = min(minimum.x, it.first.x)
            minimum.y = min(minimum.y, it.first.y)
            minimum.z = min(minimum.z, it.first.z)

            minimum.x = min(minimum.x, it.second.x)
            minimum.y = min(minimum.y, it.second.y)
            minimum.z = min(minimum.z, it.second.z)

            maximum.x = max(maximum.x, it.first.x)
            maximum.y = max(maximum.y, it.first.y)
            maximum.z = max(maximum.z, it.first.z)

            maximum.x = max(maximum.x, it.second.x)
            maximum.y = max(maximum.y, it.second.y)
            maximum.z = max(maximum.z, it.second.z)
        }

        minimumPos = minimum
        maximumPos = maximum

        for(x in 0..1) {
            for(y in 0..1) {
                for(z in 0..1) {
                    minimumAndMaxmimumPosCached.add(Vector3d(if(x == 0) minimumPos.x else maximumPos.x, if(y == 0) minimumPos.y else maximumPos.y, if(z == 0) minimumPos.z else maximumPos.z))
                }
            }
        }

    }

    fun getVecList(): List<Vector3d> {
        return ArrayList(vecList)
    }

    fun collides(start: Vector3d, finish: Vector3d, pos: Vector3d, skipIndex: Set<Int>? = null, checkPos: Boolean = false): Pair<Vector3d, Int>? {
        val direction = finish - start

        for(i in 0 until vecList.size) {
            if(skipIndex != null && skipIndex.contains(i))
                continue
            val vec = vecList[i]
            val position = positionList[i].add(pos)
            val collideRange = collideRangeList[i]

            //Bukkit.getWorld("world").spawnParticle(Particle.FLAME, position.x, position.y, position.z, 0)

            val collidePos = collide(vec, position, direction, start)
            //println("pos $i: $collidePos // $pos // $position // ${collideRange.first} ${collideRange.second} ${collidePos.subtract(pos)}")
            if(collideInBlock(collidePos.subtract(pos), collideRange.first, collideRange.second)) {
                //println("pos $i: ${collideRange.first} ${collideRange.second}")
                //println("collide: ${start} // ${finish} // ${collidePos}")
                //if(value == null)
                    //value = Pair(collidePos, i)
                //println("return $i: $collidePos") collideInBlock(collidePos.subtract(pos), collideRange.first, collideRange.second)

                if(!checkPos || collideInBlock(collidePos, start, finish)) {
                    return Pair(collidePos, i)
                }
            }
        }

        return null
        //return value
    }

    fun collideAnother(posForOriginal: Vector3d, posForValue: Vector3d, value: VectorCollideObject, equal: Boolean = true): Boolean {
        value.minimumAndMaxmimumPosCached.forEach {
            val x = posForValue.x + it.x
            val y = posForValue.y + it.y
            val z = posForValue.z + it.z

            val minX = minimumPos.x + posForOriginal.x
            val minY = minimumPos.y + posForOriginal.y
            val minZ = minimumPos.z + posForOriginal.z

            val maxX = maximumPos.x + posForOriginal.x
            val maxY = maximumPos.y + posForOriginal.y
            val maxZ = maximumPos.z + posForOriginal.z

            if(equal) {
                if (x in minX..maxX) {
                    if (y in minY..maxY) {
                        if (z in minZ..maxZ) {
                            return true
                        }
                    }
                }
            } else {
                if(minX < x && x < maxX) {
                    if(minY < y && y < maxY) {
                        if(minZ < z && z < maxZ) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun collideInBlock(result: Vector3d, min: Vector3d, max: Vector3d): Boolean {

        /*println(currentPos)

        if (currentPos.x + min.z <= result.x && result.x <= currentPos.x + max.x) {
            println("X")
            if (currentPos.y + min.y <= result.y && result.y <= currentPos.y + max.y) {
                println("Y")
                if (currentPos.z + min.z <= result.z && result.z <= currentPos.z + max.z) {
                    return true
                }
            }
        }*/
        if(if(min.x < max.x) (min.x <= result.x && result.x <= max.x) else (max.x <= result.x && result.x <= min.x)) {
            if(if(min.y < max.y) (min.y <= result.y && result.y <= max.y) else (max.y <= result.y && result.y <= min.y)) {
                if(if(min.z < max.z) (min.z <= result.z && result.z <= max.z) else (max.z <= result.z && result.z <= min.z)) {
                    return true
                }
            }
        }
//        if(min.x <= result.x && result.x <= max.x) {
//            if(min.y <= result.y && result.y <= max.y) {
//                if(min.z <= result.z && result.z <= max.z) {
//
//                }
//            }
//        }

        return false
    }
}