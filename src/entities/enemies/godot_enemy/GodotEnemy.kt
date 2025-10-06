package entities.enemies.godot_enemy

import commons.components.etc.HealthComponent
import entities.enemies.Enemy
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.core.signal0
import godot.extension.getNodeAs

@RegisterClass
class GodotEnemy : Enemy() {

    @RegisterFunction
    override fun _exitTree() {
        onDefeated.emit()
    }

    override fun getMaxHealth(): Double {
        val healthComponent = getNodeAs<HealthComponent>("%HealthComponent")
        return healthComponent?.maxHealth ?: 10.0
    }

    override fun getHealthComp(): HealthComponent {
        val healthComponent = getNodeAs<HealthComponent>("%HealthComponent")
        return healthComponent ?: HealthComponent()
    }


}