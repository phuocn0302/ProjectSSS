package entities.bosses

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.Resource
import godot.core.variantArrayOf

@RegisterClass
class BossData : Resource() {

    @Export
    @RegisterProperty
    var maxHealth: Double = 400.0

}