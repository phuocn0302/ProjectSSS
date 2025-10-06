package entities.player.states

import entities.player.PlayerState
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2

@RegisterClass
class PlayerMoveState : PlayerState() {

    private var velocity: Vector2 = Vector2.ZERO

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (player?.inputVector != Vector2.ZERO && velocity.lengthSquared() < 1) {
            states?.get("Idle")?.let { idleState ->
                stateMachine.changeState(idleState)
            }
        }
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        if (player == null) {
            return
        }

        val target = player!!.inputVector * player!!.playerData.maxMoveSpeed
        velocity = velocity.moveToward(target, player!!.playerData.moveAccel * delta)
        player!!.translate(velocity * delta )
    }
}