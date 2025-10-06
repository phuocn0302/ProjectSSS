package entities.bosses.worm

import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Dictionary

@RegisterClass
open class WormBossState : State() {
    lateinit var boss: WormBoss
    lateinit var states: Dictionary<String, State>

    @RegisterFunction
    override fun setup(actor: Entity, stateMachine: StateMachine) {
        super.setup(actor, stateMachine)
        boss = this.actor as WormBoss
        states = stateMachine.states
    }
}



