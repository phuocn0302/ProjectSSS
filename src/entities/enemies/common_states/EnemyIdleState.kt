package entities.enemies.common_states

import commons.singletons.Utils
import commons.state_machine.State
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.connect
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
            field = value
            remainingState.clear()
        }

    private var lastState: State? = null
    private var secondLastState: State? = null
    private var remainingState = variantArrayOf<State>()

    @RegisterFunction
    override fun enter() {
        val timer = Utils.createTimer(this, idleTime)
        timer!!.timeout.connect {
            stateMachine.changeState(getRandomState())
        }
    }

    private fun getRandomState(): State {
        if (states.isEmpty()) {
            return this
        }

        if (remainingState.isEmpty()) {
            remainingState = states.duplicate(false)
        }


        var candidates: List<State> = remainingState.filter { s ->
            s != lastState || s != secondLastState
        }

        if (candidates.isEmpty()) {
            candidates = if (remainingState.any { it != lastState }) {
                remainingState.filter { it != lastState }
            } else {
                remainingState.toList()
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