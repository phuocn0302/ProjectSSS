package entities.projectiles.spawners

import commons.object_pooling.ObjectPool
import entities.projectiles.Projectile
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.common.util.TAU
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class CircleProjectileSpawnerData : ProjectileSpawnerData() {

    @Export
    @RegisterProperty
    var numberOfProjectiles: Int = 10

    @Export
    @RegisterProperty
    var rotatePattern: Boolean = false

    @Export
    @RegisterProperty
    var rotateSpeed: Double = 20.0

    @RegisterFunction
    override fun spawn(projectilePool: ObjectPool, spawner: ProjectileSpawner) {
        val angle = TAU / numberOfProjectiles

        for (i in 0 until numberOfProjectiles) {
            val proj = projectilePool.getInstance() as Projectile
            proj.direction = this.spawnDirection.rotated(angle * i)
            proj.globalPosition = spawner.globalPosition
        }

        if (rotatePattern) {
            spawnDirection = spawnDirection.rotated(GD.degToRad(rotateSpeed))
        }
    }
}
