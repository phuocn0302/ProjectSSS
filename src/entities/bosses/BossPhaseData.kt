package entities.bosses

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.Resource
import godot.core.NodePath
import godot.core.variantArrayOf
import godot.global.GD

@RegisterClass
class BossPhaseData : Resource() {

    @Export
    @RegisterProperty
    var healthPercentage: Double = 100.0
        set(value) {
            field = GD.clamp(value, 1, 100)
        }

    @Export
    @RegisterProperty
    var states = variantArrayOf<NodePath>()

    @Export
    @RegisterProperty
    var firstState: NodePath = NodePath()
}