package commons.components.etc


import commons.components.Component
import entities.Entity
import godot.annotation.*
import godot.api.Node2D
import godot.api.PackedScene
import godot.core.signal0
import godot.core.signal1
import godot.global.GD

@RegisterClass
class HealthComponent : Component() {

    @RegisterSignal
    val healthDepleted by signal1<Double>()

    @RegisterSignal
    val healthRestored by signal1<Double>()

    @RegisterSignal
    val healthReachedZero by signal0()

    @Export
    @RegisterProperty
    var maxHealth: Double = 100.0

    @Export
    @RegisterProperty
    var dieSfx: PackedScene? = null

    @Export
    @RegisterProperty
    var queueFreeOnDie: Boolean = true

    @RegisterProperty
    var currentHealth: Double = 0.0

    @RegisterFunction
    override fun _ready() {
        currentHealth = maxHealth
    }

    @RegisterFunction
    fun takeDamage(damageAmount: Double) {
        currentHealth -= damageAmount
        healthDepleted.emit(damageAmount)

        if (currentHealth <= 0) {
            currentHealth = 0.0
            healthReachedZero.emit()
            die()
        }
    }

    @RegisterFunction
    fun restoreHealth(healAmount: Double) {
        val oldHealth = currentHealth
        currentHealth = GD.clamp(currentHealth + healAmount, 0.0, maxHealth)
        val restored = currentHealth - oldHealth
        if (restored > 0) {
            healthRestored.emit(restored)
        }
    }

    @RegisterFunction
    fun die() {
        dieSfx?.let { sfxScene ->
            val sfx = sfxScene.instantiate() as Node2D
            sfx.globalPosition = (entity as Entity).globalPosition
            getTree()?.currentScene?.addChild(sfx)
        }

        if (queueFreeOnDie) {
            entity?.queueFree()
        }
    }
}
