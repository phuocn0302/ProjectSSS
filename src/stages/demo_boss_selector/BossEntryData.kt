package stages.demo_boss_selector

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.PackedScene
import godot.api.Resource
import godot.api.Texture2D

@RegisterClass
class BossEntryData : Resource() {

    @Export
    @RegisterProperty
    var bossName: String = ""

    @Export
    @RegisterProperty
    var bossMugshot: Texture2D? = null

    @Export
    @RegisterProperty
    var bossScene: PackedScene? = null
}
