package entities.projectiles.types

import entities.projectiles.Projectile
import entities.projectiles.ProjectileSpawner
import entities.projectiles.spawners.ProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
class SpawnerProjectile : Projectile() {

    private val projectileSpawner: ProjectileSpawner = ProjectileSpawner()

    @RegisterFunction
    override fun _ready() {
        super._ready()
        setupSpawner()
    }

    @RegisterFunction
    override fun activate() {
        super.activate()

        projectileSpawner.active = true
    }

    @RegisterFunction
    override fun deactivate() {
        super.deactivate()

        projectileSpawner.active = false
    }

    private fun setupSpawner() {
        if (projectileData !is SpawnerProjectileData) {
            return
        }

        val pData = projectileData as SpawnerProjectileData
        projectileSpawner.spawnerData = pData.projectileSpawnerData
        projectileSpawner.projectileType = pData.projectileType
        projectileSpawner.projectileData = pData.projectileData

        addChild(projectileSpawner)
    }
}