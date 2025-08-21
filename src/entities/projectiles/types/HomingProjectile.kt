package entities.projectiles.types

import commons.singletons.Utils
import entities.projectiles.Projectile
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.core.Vector2
import godot.core.connect
import godot.global.GD

@RegisterClass
class HomingProjectile : Projectile() {

    private var targetGroup: String = "player"
    private var rotateSpeed: Double = 90.0
    private var chaseDelay: Double = 0.5

    private var isChasing: Boolean = false

    @RegisterFunction
    override fun _ready() {
        super._ready()

        if (projectileData is HomingProjectileData) {
            val pData = projectileData as HomingProjectileData
            targetGroup = pData.targetGroup
            rotateSpeed = pData.rotateSpeed
            chaseDelay = pData.chaseDelay
        }

        val timer = Utils.createTimer(this, chaseDelay)
        timer?.timeout?.connect {
            isChasing = true
        }
    }

    @RegisterFunction
    override fun move(delta: Double) {
        if (isChasing) {
            chase(delta)
        } else {
            super.move(delta)
        }
    }

    private fun chase(delta: Double) {
        val targetDir = getDirectionToTarget()

        val maxAngle = GD.degToRad(rotateSpeed) * delta

        val angle = GD.clamp(direction.angleTo(targetDir), -maxAngle, maxAngle)

        direction = direction.rotated(angle).normalized()

        globalPosition += direction * projectileData!!.speed * delta
        globalRotation = direction.angle().toFloat()
    }

    private fun getDirectionToTarget(): Vector2 {
        val targets = getTree()?.getNodesInGroup(targetGroup)
            ?.filterIsInstance<Node2D>()
            ?: return direction

        val closest = getClosestTarget(targets, globalPosition) ?: return direction
        return (closest.globalPosition - globalPosition).normalized()
    }

    private fun getClosestTarget(targets: List<Node2D>, spawnPosition: Vector2): Node2D? {
        var closest: Node2D? = null
        var minDist = Double.POSITIVE_INFINITY

        for (t in targets) {
            val dist = spawnPosition.distanceSquaredTo(t.globalPosition)
            if (dist < minDist) {
                minDist = dist
                closest = t
            }
        }

        return closest
    }
}