package commons.state_machine

import entities.Entity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.InputEvent
import godot.api.Node

@RegisterClass
class StateMachine : Node() {

    @Export
    @RegisterProperty
    var active: Boolean = true

    @Export
    @RegisterProperty
    var actor: Entity? = null

    @Export
    @RegisterProperty
    var startingState: State? = null

    @Export
    @RegisterProperty
    var states: MutableMap<String, State> = mutableMapOf()

    var nextStateRequested: State? = null
    var currentState: State? = null

    @RegisterFunction
    fun setup(actor: Entity) {
        for (child in getChildren()) {
            if (child is State) {
                states[child.name.toString()] = child
                child.setup(actor, this)
            }
        }
        startingState?.let { changeState(it) }
    }

    @RegisterFunction
    fun changeState(newState: State) {
        if (!active) return

        currentState?.exit()
        currentState = newState

        if (nextStateRequested != null) {
            currentState = nextStateRequested
            nextStateRequested = null
        }

        currentState?.enter()
    }

    @RegisterFunction
    fun physicsProcess(delta: Double) {
        if (!active) return
        currentState?.processPhysics(delta)
    }

    @RegisterFunction
    fun frameProcess(delta: Double) {
        if (!active) return
        currentState?.processFrame(delta)
    }

    @RegisterFunction
    fun inputProcess(event: InputEvent) {
        if (!active) return
        currentState?.processInput(event)
    }
}
