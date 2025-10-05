package entities.player

import commons.state_machine.State
import commons.state_machine.StateMachine
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Dictionary
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class MobileInputHandler : Node() {

    @Export
    @RegisterProperty
    var dashCooldownTimer: Timer? = null

    @RegisterProperty
    var player: Player? = null

    @RegisterProperty
    var stateMachine: StateMachine? = null

    private var playerData: PlayerData? = null
    private var states: Dictionary<String, State>? = null

    private val flickDistance = 20.0
    private val flickTime = 0.2
    private val minDragThreshold = 0.2

    private var touchLastPos: Vector2 = Vector2.ZERO
    private var touchActive: Boolean = false
    private var touchStartPos: Vector2 = Vector2.ZERO
    private var touchStartTime: Double = 0.0

    @RegisterFunction
    override fun _ready() {
        GD.assert(dashCooldownTimer != null)
    }

    @RegisterFunction
    fun setup(player: Player, stateMachine: StateMachine) {
        this.player = player
        this.playerData = player.playerData
        this.stateMachine = stateMachine
        this.states = stateMachine.states
    }

    @RegisterFunction
    override fun _input(event: InputEvent?) {
        if (event == null || player == null) {
            return
        }

        when (event) {
            is InputEventScreenTouch -> handleScreenTouch(event)
            is InputEventScreenDrag -> handleScreenDrag(event)
        }

        stateMachine?.inputProcess(event)
    }

    @RegisterFunction
    fun handleScreenTouch(event: InputEventScreenTouch) {
        if (event.pressed) {
            touchActive = true
            touchLastPos = event.position
            touchStartPos = event.position
            touchStartTime = Time.getTicksMsec() / 1000.0
        } else {
            if (touchActive) {
                handleTouchEnd(event)
                touchActive = false
            }
        }
    }

    @RegisterFunction
    fun handleTouchEnd(event: InputEventScreenTouch) {
        val elapsed = (Time.getTicksMsec() / 1000.0) - touchStartTime
        val totalDelta = event.position - touchStartPos
        val distance = totalDelta.length()

        if (elapsed <= flickTime && distance >= flickDistance && (dashCooldownTimer == null || dashCooldownTimer?.isStopped() == true)) {
            val dashDir = totalDelta.normalized()
            player!!.inputVector = dashDir
            states?.get("Dash")?.let { dashState ->
                stateMachine?.changeState(dashState)
            }
            player!!.inputVector = Vector2.ZERO
            return
        }

        player!!.inputVector = Vector2.ZERO
    }

    @RegisterFunction
    fun handleScreenDrag(event: InputEventScreenDrag) {
        if (!touchActive || player == null) return

        val dragDelta = event.position - touchLastPos
        touchLastPos = event.position

        val dragLengthSqr = dragDelta.lengthSquared()
        if (dragLengthSqr <= minDragThreshold) {
            player!!.inputVector = Vector2.ZERO
            return
        }

        player!!.inputVector = dragDelta
        if (player!!.inputVector.length() > 1.0) {
            player!!.inputVector = player!!.inputVector.normalized()
        }
    }
}

