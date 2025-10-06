package entities.bosses.capy

import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.Entity
import entities.bosses.godot.GodotBoss
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Dictionary

@RegisterClass
open class CapyBossState : State() {
    lateinit var boss: CapyBoss
    lateinit var states: Dictionary<String, State>

    @RegisterFunction
    override fun setup(actor: Entity, stateMachine: StateMachine) {
        super.setup(actor, stateMachine)

        boss = this.actor as CapyBoss
        states = stateMachine.states
    }
}