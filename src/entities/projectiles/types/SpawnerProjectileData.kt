package entities.projectiles.types

import entities.projectiles.ProjectileData
import entities.projectiles.ProjectileFactory
import entities.projectiles.spawners.ProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class SpawnerProjectileData : ProjectileData() {

    @Export
    @RegisterProperty
    var projectileSpawnerData: ProjectileSpawnerData = ProjectileSpawnerData()

    @Export
    @RegisterProperty
    var projectileType: ProjectileFactory.Type = ProjectileFactory.Type.NORMAL

    @Export
    @RegisterProperty
    var projectileData: ProjectileData = ProjectileData()
}