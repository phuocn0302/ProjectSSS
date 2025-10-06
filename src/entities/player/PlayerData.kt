package entities.player

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.Resource

@RegisterClass
class PlayerData : Resource() {

    @Export
    @RegisterProperty
    var maxMoveSpeed: Double = 150.0

    @Export
    @RegisterProperty
    var minMoveSpeed: Double = 50.0

    @Export
    @RegisterProperty
    var moveAccel: Double = 2000.0

    @Export
    @RegisterProperty
    var dashSpeed: Double = 600.0

    @Export
    @RegisterProperty
    var dashTime: Double = 0.1

    @Export
    @RegisterProperty
    var dashCooldown: Double = 0.4

    @Export
    @RegisterProperty
    var shootInterval: Double = 0.2

}