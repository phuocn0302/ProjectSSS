package commons.components.etc

import commons.components.ComponentArea2D
import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.annotation.RegisterFunction
import godot.api.Area2D
import godot.api.Node2D
import godot.api.PackedScene
import godot.core.Vector2
import godot.core.signal0

@RegisterClass
class HitboxComponent : ComponentArea2D() {

    @RegisterProperty
    var damage: Double = 1.0

    @RegisterProperty
    var queueFreeOnContact: Boolean = false

    @RegisterProperty
    var queueFreeSfx: PackedScene? = null

    @RegisterProperty
    var onHitSfx: PackedScene? = null

    @RegisterSignal
    val hit by signal0()

    @RegisterFunction
    override fun _ready() {
        super._ready()
        setDeferred("monitorable", false)
    }

    @RegisterFunction
    override fun onAreaEntered(area: Area2D) {
        if (area is HurtboxComponent) {
            area.takeDamage(damage)
            hit.emit()

            val owner2D = owner as? Entity

            // On hit SFX
            onHitSfx?.let { sfxScene ->
                val sfx = sfxScene.instantiate() as? Area2D ?: return@let
                sfx.globalPosition = owner2D?.globalPosition ?: Vector2.ZERO
                getTree()?.currentScene?.addChild(sfx)
            }

            // Queue-free on contact
            if (queueFreeOnContact) {
                queueFreeSfx?.let { sfxScene ->
                    val sfx = sfxScene.instantiate() as? Area2D ?: return@let
                    sfx.globalPosition = owner2D?.globalPosition ?: Vector2.ZERO
                    getTree()?.currentScene?.addChild(sfx)
                }
                owner2D?.queueFree()
            }
        }
    }
}
