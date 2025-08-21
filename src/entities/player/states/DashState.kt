package entities.player.states

import entities.player.PlayerState
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.api.Timer
import godot.core.Vector2
import godot.core.signal0
import godot.core.times
import godot.extension.getNodeAs

@RegisterClass
class PlayerDashState : PlayerState() {

    @RegisterSignal
    val onDashed by signal0()

    @RegisterSignal
    val onDashEnded by signal0()

    private lateinit var dashTimer: Timer
    private lateinit var dashCooldownTimer: Timer

    private var dashDirection: Vector2 = Vector2.ZERO

    @RegisterFunction
    override fun _ready() {
        dashTimer = getNodeAs("DashTimer")!!
        dashCooldownTimer = getNodeAs("DashCooldownTimer")!!

        dashTimer.timeout.connect(this, PlayerDashState::onDashTimerTimeout)
    }

    @RegisterFunction
    override fun enter() {
        dashDirection = if (player?.inputVector == Vector2.ZERO) {
            Vector2.UP
        } else {
            player!!.inputVector
        }

        dashTimer.start(player!!.playerData.dashTime)
        onDashed.emit()
    }

    @RegisterFunction
    override fun exit() {
        onDashEnded.emit()

        if (dashCooldownTimer.isStopped()) {
            dashCooldownTimer.start(player!!.playerData.dashCooldown)
        }
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        player?.translate(player!!.playerData.dashSpeed * dashDirection * delta)
    }

    @RegisterFunction
    fun onDashTimerTimeout() {
        states?.get("Idle")?.let { idleState ->
            stateMachine.changeState(idleState)
        }
    }
}