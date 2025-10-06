package commons.state_machine

import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.InputEvent
import godot.api.Node

@RegisterClass
open class State : Node() {

    lateinit var stateMachine: StateMachine
    lateinit var actor: Entity

    @RegisterFunction
    override fun _exitTree() {
        if (stateMachine.active) {
            exit()
        }
    }

    @RegisterFunction
    open fun setup(actor: Entity, stateMachine: StateMachine) {
        this.actor = actor
        this.stateMachine = stateMachine
    }

    @RegisterFunction
    open fun enter() {}

    @RegisterFunction
    open fun exit() {}

    @RegisterFunction
    open fun processFrame(delta: Double) {}

    @RegisterFunction
    open fun processPhysics(delta: Double) {}

    @RegisterFunction
    open fun processInput(event: InputEvent) {}
}
