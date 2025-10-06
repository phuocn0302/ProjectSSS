package entities.player.states

import entities.player.PlayerState
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2

@RegisterClass
class PlayerIdleState : PlayerState() {

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (player?.inputVector != Vector2.ZERO) {
            states?.get("Move")?.let { moveState ->
                stateMachine.changeState(moveState)
            }
        }
    }
}