package stages.demo_boss_selector

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Label
import godot.api.Sprite2D
import godot.api.Texture2D
import godot.api.TextureButton
import godot.core.Vector2
import godot.extension.getNodeAs

@RegisterClass
class BossEntry : TextureButton() {

    @Export
    @RegisterProperty
    var bossName: String = ""

    @Export
    @RegisterProperty
    var bossMugshot: Texture2D? = null

    private lateinit var bossNameLabel: Label
    private lateinit var bossMugshotSprite: Sprite2D

    @RegisterFunction
    override fun _ready() {
        bossNameLabel = getNodeAs("%BossName")!!
        bossMugshotSprite = getNodeAs("%BossMugshot")!!

        assert(bossName.isNotEmpty() && bossMugshot != null)
        assert(bossMugshot?.getSize() == Vector2(30.0, 30.0))

        bossNameLabel.text = bossName
        bossMugshotSprite.texture = bossMugshot
    }
}
