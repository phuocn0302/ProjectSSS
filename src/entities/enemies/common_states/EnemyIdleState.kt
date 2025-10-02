package entities.enemies.common_states

import commons.singletons.Utils
import commons.state_machine.State
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.connect
import godot.core.toVariantArray
import godot.core.variantArrayOf
import godot.global.GD

@RegisterClass
open class EnemyIdleState : State() {

    @Export
    @RegisterProperty
    var idleTime: Double = 1.0

    @Export
    @RegisterProperty
    var states = variantArrayOf<State>()
        set(value) {
            val filtered = value.filterIsInstance<State>()
            if (filtered.size != value.size) {
                GD.pushWarning("[EnemyIdleState] Some entries in 'states' are not State instances and were ignored.")
            }
            field = filtered.toVariantArray()
            remainingState.clear()
        }

    private var lastState: State? = null
    private var secondLastState: State? = null
    private var remainingState = variantArrayOf<State>()

    @RegisterFunction
    override fun enter() {
        val idleTween = createTween()
        idleTween?.tweenInterval(idleTime)
        idleTween?.finished?.connect {
            stateMachine.changeState(getRandomState())
        }
    }

    private fun getRandomState(): State {
        val validStates = states.filterIsInstance<State>()
        if (validStates.isEmpty()) {
            GD.pushWarning("[EnemyIdleState] No valid states to transition to.")
            return this
        }

        if (remainingState.isEmpty()) {
            remainingState = validStates.toVariantArray()
        }

        var candidates = remainingState
            .filterIsInstance<State>()
            .filter { it != lastState || it != secondLastState }

        if (candidates.isEmpty()) {
            candidates = if (remainingState.any { it != lastState }) {
                remainingState.filterIsInstance<State>().filter { it != lastState }
            } else {
                remainingState.filterIsInstance<State>()
            }
        }

        val randomState = candidates.random()
        remainingState.erase(randomState)

        secondLastState = lastState
        lastState = randomState

        GD.print("[Idle] Next state: ", randomState)

        return randomState
    }
}
