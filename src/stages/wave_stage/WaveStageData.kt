package stages.wave_stage

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.PackedScene
import godot.api.Resource
import godot.core.variantArrayOf

@RegisterClass
class WaveStageData : Resource() {

    @Export
    @RegisterProperty
    var waves = variantArrayOf<WaveData>()

    @Export
    @RegisterProperty
    var interWaveDelaySeconds: Double = 2.0

    @Export
    @RegisterProperty
    var bossScene: PackedScene? = null
}
