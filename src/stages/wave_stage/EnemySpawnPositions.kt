package stages.wave_stage

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.Resource
import godot.core.Vector2
import godot.core.variantArrayOf

@RegisterClass
class EnemySpawnPositions : Resource() {

    @Export
    @RegisterProperty
    var positions = variantArrayOf<Vector2>()
}
