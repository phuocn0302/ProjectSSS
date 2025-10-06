package entities.projectiles.spawners

import commons.object_pooling.ObjectPool
import entities.projectiles.Projectile
import entities.projectiles.ProjectileData
import entities.projectiles.ProjectileFactory
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Resource
import godot.core.Vector2

@RegisterClass
open class ProjectileSpawnerData : Resource() {
    @Export
    @RegisterProperty
    var spawnDirection: Vector2 = Vector2.UP

    @Export
    @RegisterProperty
    var spawnInterval: Double = 0.5

    @RegisterFunction
    open fun spawn(projectilePool: ObjectPool, spawner: ProjectileSpawner) {
        val proj: Projectile = projectilePool.getInstance() as Projectile

        proj.direction = spawnDirection
        proj.globalPosition = spawner.globalPosition
    }
}