package entities.player

import commons.state_machine.StateMachine
import entities.Entity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector2
import godot.extension.getNodeAs

@RegisterClass
class Player : Entity() {

    @Export
    @RegisterProperty
    var playerData: PlayerData = PlayerData()

    @RegisterProperty
    var inputVector:  Vector2 = Vector2.ZERO

    private lateinit var stateMachine: StateMachine
    private lateinit var inputHandler: PlayerInputHandler

    @RegisterFunction
    override fun _enterTree() {
        addToGroup("player")
    }

    @RegisterFunction
    override fun _ready() {
        stateMachine = getNodeAs("StateMachine")!!
        inputHandler = getNodeAs("InputHandler")!!

        stateMachine.setup(this)
        inputHandler.setup(this, stateMachine)
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        stateMachine.frameProcess(delta )
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        stateMachine.physicsProcess(delta)
    }
}