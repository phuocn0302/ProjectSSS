package entities.enemies

import commons.components.etc.HealthComponent
import commons.state_machine.StateMachine
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.connect
import godot.extension.getNodeAs

@RegisterClass
class GenericEnemy : Enemy() {

    @Export
    @RegisterProperty
    var stateMachine: StateMachine? = null

    private lateinit var healthComponent: HealthComponent

    @RegisterFunction
    override fun _ready() {
        healthComponent = getNodeAs("%HealthComponent")!!

        healthComponent.healthReachedZero.connect {
            onDefeated.emit()
        }

        stateMachine?.setup(this)
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        stateMachine?.frameProcess(delta)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        stateMachine?.physicsProcess(delta)
    }

    override fun getMaxHealth(): Double {
        return healthComponent.maxHealth
    }

    override fun getHealthComp(): HealthComponent {
        return healthComponent
    }

    override fun getScorePoint(): Int {
        return 120
    }

}