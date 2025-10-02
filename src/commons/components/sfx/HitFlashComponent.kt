package commons.components.sfx

import commons.components.Component
import commons.components.etc.HealthComponent
import commons.singletons.Utils
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.AnimatedSprite2D
import godot.api.Node2D
import godot.api.SceneTreeTimer
import godot.api.Sprite2D
import godot.core.connect


@RegisterClass
class HitFlashComponent : Component() {

    @Export
    @RegisterProperty
    var sprite: Node2D? = null

    @Export
    @RegisterProperty
    var healthComponent: HealthComponent? = null

    @Export
    @RegisterProperty
    var flashTime: Double = 0.1

    @RegisterFunction
    override fun _ready() {
        require(sprite != null) { "Sprite must not be null" }
        require(sprite is Sprite2D || sprite is AnimatedSprite2D) { "Sprite must be Sprite2D or AnimatedSprite2D" }

        healthComponent?.healthDepleted?.connect { _ -> flash() }

        resetFlash()
    }

    @RegisterFunction
    fun flashNoArgs() {
        flash()
    }

    @RegisterFunction
    fun flash() {
        val mat = sprite?.material ?: return
        mat.set("shader_parameter/flash_strength", 0.5)

        val tween = createTween()
        tween?.tweenInterval(flashTime)
        tween?.finished?.connect(this, HitFlashComponent::resetFlash)
    }

    @RegisterFunction
    fun resetFlash() {
        val mat = sprite?.material ?: return
        mat.set("shader_parameter/flash_strength", 0.0)
    }
}
