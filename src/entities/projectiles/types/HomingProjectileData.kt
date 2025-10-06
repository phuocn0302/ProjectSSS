package entities.projectiles.types

import entities.projectiles.ProjectileData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty

@RegisterClass
class HomingProjectileData : ProjectileData() {

    @Export
    @RegisterProperty
    var targetGroup: String = "player"

    @Export
    @RegisterProperty
    var rotateSpeed: Double = 90.0

    @Export
    @RegisterProperty
    var chaseDelay: Double = 0.5

}