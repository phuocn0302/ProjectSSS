package entities.enemies.common_states

import commons.components.etc.HurtboxComponent
import commons.state_machine.State
import godot.annotation.*
import godot.core.Vector2
import godot.core.signal0

@RegisterClass
open class EnemyEntranceState : State() {

    @RegisterSignal
    val onFinished by signal0()

    @Export
    @RegisterProperty
    var speed: Double = 20.0

    @Export
    @RegisterProperty
    var destinationPos: Vector2 = Vector2(90, 60)

    @Export
    @RegisterProperty
    var hurtboxComponent: HurtboxComponent? = null

    @Export
    @RegisterProperty
    var nextState: State? = null

    private var isEnded: Boolean = false

    @RegisterFunction
    override fun enter() {
        hurtboxComponent?.active = false
    }

    @RegisterFunction
    override fun exit() {
        hurtboxComponent?.active = true
    }

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (!isEnded && actor.globalPosition.distanceSquaredTo(destinationPos) < 0.5) {
            isEnded = true
            onFinished.emit()

            nextState?.let { nextState ->
                stateMachine.changeState(nextState)
            }
        }
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        actor.globalPosition = actor.globalPosition.moveToward(
            destinationPos,
            speed * delta
        )
    }
}