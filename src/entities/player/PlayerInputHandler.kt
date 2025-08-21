package entities.player

import commons.state_machine.State
import commons.state_machine.StateMachine
import godot.annotation.*
import godot.api.Input
import godot.api.InputEvent
import godot.api.Node
import godot.api.Timer
import godot.core.Dictionary
import godot.core.signal0
import godot.global.GD

@RegisterClass
class PlayerInputHandler : Node() {

    @RegisterSignal
    val onShootRequested by signal0()

    @Export
    @RegisterProperty
    var shootCooldownTimer: Timer? = null

    @Export
    @RegisterProperty
    var dashCooldownTimer: Timer? = null

    @RegisterProperty
    var player: Player? = null

    @RegisterProperty
    var stateMachine: StateMachine? = null

    private var playerData: PlayerData? = null
    private var states: Dictionary<String, State>? = null

    private var isShooting: Boolean = false
    private var canShoot: Boolean = true

    @RegisterFunction
    override fun _ready() {
        GD.assert((shootCooldownTimer != null) && (dashCooldownTimer != null))
    }

    @RegisterFunction
    fun setup(player: Player, stateMachine: StateMachine) {
        this.player = player
        this.playerData = player.playerData
        this.stateMachine = stateMachine
        this.states = stateMachine.states

        shootCooldownTimer?.waitTime = playerData!!.shootInterval
        shootCooldownTimer?.timeout?.connect(this, PlayerInputHandler::onShootCooldownTimeout)
    }

    @RegisterFunction
    override fun _input(event: InputEvent?) {
        if (event == null) {
            return
        }

        player!!.inputVector = Input.getVector(
            "move_left",
            "move_right",
            "move_up",
            "move_down"
        )

        if (event.isActionPressed("dash") && dashCooldownTimer?.isStopped() == true) {
            states?.get("Dash")?.let { dashState ->
                stateMachine?.changeState(dashState)
            }
        }

        if (event.isActionPressed("shoot") && canShoot) {
            isShooting = true
            canShoot = false
            shoot()
        }

        if (event.isActionReleased("shoot")) {
            isShooting = false
        }

        stateMachine?.inputProcess(event)
    }

    @RegisterFunction
    fun onShootCooldownTimeout() {
        canShoot = true
        if (isShooting) {
            shoot()
        }
    }
    
    private fun shoot() {
        onShootRequested.emit()
        shootCooldownTimer?.start()
    }
}