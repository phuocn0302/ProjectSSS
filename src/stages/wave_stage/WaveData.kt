package stages.wave_stage

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.PackedScene
import godot.api.Resource
import godot.core.Vector2
import godot.core.variantArrayOf

@RegisterClass
class WaveData : Resource() {

    @Export
    @RegisterProperty
    var enemyScene: PackedScene? = null

    @Export
    @RegisterProperty
    var waveDurationSeconds: Double = 10.0

    @Export
    @RegisterProperty
    var spawnIntervalSeconds: Double = 1.0

    @Export
    @RegisterProperty
    var enemySpawnPositions: EnemySpawnPositions? = null
}
