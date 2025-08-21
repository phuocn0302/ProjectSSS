package entities.player

import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Dictionary


@RegisterClass
open class PlayerState : State() {
    var player: Player? = null
    var states: Dictionary<String, State>? = null

    @RegisterFunction
    override fun setup(actor: Entity, stateMachine: StateMachine) {
        super.setup(actor, stateMachine)

        player = this.actor as Player
        states = stateMachine.states
    }
}