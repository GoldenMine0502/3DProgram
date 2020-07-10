package kr.goldenmine.collision

//import com.boydti.fawe.bukkit.wrapper.AsyncWorld
//import kr.theterroronline.util.physics.collides.ICollide
//import org.bukkit.Location
//import org.bukkit.Particle
//import org.bukkit.World
//import org.bukkit.entity.Player
import kr.goldenmine.PointStorage
import kr.theterroronline.util.physics.Face
import kr.theterroronline.util.physics.Vector3d
import java.util.*


class PhysicalParticle constructor(
    private val pointStorage: PointStorage,
    private val sleep: Long = 10,
    private val gravity: Double = 0.001,
    private val handPower: Double = 1.0,
    private val bouncePercent: Double = 0.9,
    private val yBouncePercent: Double = 0.4,
    private val activeTimeInMillis: Long = 60000,
    private val particleCount: Int = 5
) {

    private val collides = ArrayList<ICollide>()
    private val collideReflects = HashMap<ICollide, Boolean>()
    private val collideEvents = HashMap<ICollide, ((collide: ICollide, type: Int, obj: Any?) -> (Unit))?>()

    lateinit var physicalObject: PhysicalObject

    private var stopped = false

    fun addCollide(
        collide: ICollide,
        collideReflect: Boolean,
        onCollide: ((collide: ICollide, type: Int, obj: Any?) -> Unit)? = null
    ) {
        collides.add(collide)
        collideReflects[collide] = collideReflect
        collideEvents[collide] = onCollide
    }

    fun getCurrentPos(): Vector3d {
        return physicalObject.currentPos
    }

    fun getLastPos(): Vector3d {
        return physicalObject.lastPos
    }


    fun perform(firstLoc: Vector3d, direction: Vector3d) {
        stopped = false

//        val asyncWorld = AsyncWorld.wrap(world)

        physicalObject =
            PhysicalObject(Vector3d(firstLoc, true), Vector3d(direction.multiply(handPower), true), gravity)

        val started = System.currentTimeMillis()

        var count = 0

        while (!stopped && System.currentTimeMillis() - started <= activeTimeInMillis) {
            //com.boydti.fawe.bukkit.wrapper.AsyncChunk
            physicalObject.next()
            if (count >= particleCount) {
                //CraftWorld
//                asyncWorld.spawnParticle(
//                    particle,
//                    physicalObject.currentPos.x,
//                    physicalObject.currentPos.y,
//                    physicalObject.currentPos.z,
//                    0
//                )
                count = 0
            }
            count++

            if (physicalObject.currentPos.y < 0)
                break

            for (collide in collides) {
                val result = collide.collide(pointStorage, physicalObject.lastPos, physicalObject.currentPos)


                if (result != null) {
                    physicalObject.currentPos.copyFrom(result.collideVec)

                    val collideReflect = collideReflects[collide]!!

                    val dir = physicalObject.velocity

                    collideEvents[collide]?.invoke(collide, result.result, result.meta)

                    if (collideReflect) {
                        when (result.face) {
                            Face.X, Face.XPlus -> {
                                dir.x = -dir.x * bouncePercent
                            }
                            Face.Y, Face.YPlus -> {
                                dir.x = dir.x * bouncePercent
                                dir.y = -dir.y * yBouncePercent
                                dir.z = dir.z * bouncePercent

                                physicalObject.currentGravity *= yBouncePercent

                                if (dir.y in 0.0..0.01) {
                                    dir.y = 0.01
                                }
                                if (dir.y in -0.01..0.0) {
                                    dir.y = -0.01
                                }
                            }
                            Face.Z, Face.ZPlus -> {
                                dir.z = -dir.z * bouncePercent
                            }
                        }
                    }
                }
            }

            Thread.sleep(sleep)
        }
    }

    fun stop() {
        stopped = true
    }
}