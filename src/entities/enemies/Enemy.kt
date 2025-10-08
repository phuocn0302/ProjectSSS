package entities.enemies

import commons.components.etc.HealthComponent
import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.core.signal0

@RegisterClass
abstract class  Enemy : Entity() {

    @RegisterSignal
    val onDefeated by signal0()

    @RegisterFunction
    override fun _enterTree() {
        addToGroup("enemy")
    }

    abstract fun getMaxHealth() : Double
    abstract fun getHealthComp() : HealthComponent
    abstract fun getScorePoint() : Int
}