package entities.projectiles.spawners

import commons.object_pooling.ObjectPool
import entities.projectiles.Projectile
import entities.projectiles.ProjectileData
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Path2D
import godot.api.PathFollow2D
import godot.core.NodePath
import godot.core.VariantArray
import godot.core.Vector2
import godot.core.variantArrayOf
import godot.global.GD
import kotlin.math.max


@RegisterClass
class PatternProjectileSpawnerData : ProjectileSpawnerData() {

    @Export
    @RegisterProperty
    var numberOfProjectile: Int = 10

    @Export
    @RegisterProperty
    var travelTime: Double = 0.5

    @Export
    @RegisterProperty
    // Export Path2D directly somehow not working
    var patternNodePath: NodePath = NodePath()

    private var cachedDirections = variantArrayOf<Vector2>()
    private var cachedDistances = variantArrayOf<Double>()

    @RegisterFunction
    override fun spawn(projectilePool: ObjectPool, spawner: ProjectileSpawner) {
        if (cachedDirections.size != numberOfProjectile) {
            cachedPattern(spawner)
        }

        if (cachedDirections.isEmpty()) {
            return
        }

        for (i in 0 until numberOfProjectile) {
            val proj = projectilePool.getInstance() as Projectile
            val projData = proj.projectileData!!.duplicate() as ProjectileData

            projData.speed = cachedDistances[i] / travelTime

            proj.projectileData = projData
            proj.globalPosition = spawner.globalPosition
            proj.direction = cachedDirections[i]
        }
    }

    private fun cachedPattern(spawner: ProjectileSpawner) {

        val pattern = spawner.getNodeOrNull(patternNodePath) as Path2D?

        if (pattern == null) {
            GD.pushError("Pattern is null")
            return
        }

        val curve = pattern.curve

        if (curve == null) {
            GD.pushError("Pattern curve is null")
            return
        }
        if (curve.pointCount == 0) {
            GD.pushError("Pattern curve has no points")
            return
        }

        cachedDirections.clear()
        cachedDistances.clear()

        for (i in 0 until numberOfProjectile) {
            val follow = PathFollow2D()
            pattern.addChild(follow)
            follow.progressRatio = (i.toFloat() / max(1, numberOfProjectile - 1))
            val patternPos = follow.globalPosition
            cachedDirections.append(spawner.globalPosition.directionTo(patternPos))
            cachedDistances.append(patternPos.distanceTo(spawner.globalPosition))
            follow.queueFree()
        }
    }
}