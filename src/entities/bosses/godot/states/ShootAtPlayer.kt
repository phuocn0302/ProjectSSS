package entities.bosses.godot.states

import commons.singletons.Utils
import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.SceneTreeTimer

@RegisterClass
class GodotBossShootAtPlayerState : GodotBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 2.0

    private lateinit var timer: SceneTreeTimer

    @RegisterFunction
    override fun enter() {
        boss.targetProjectileSpawner.active = true

        timer = Utils.createTimer(this, duration)!!
        timer.timeout.connect(this, GodotBossShootAtPlayerState::changeState)
    }

    @RegisterFunction
    override fun exit() {
        boss.targetProjectileSpawner.active = false

        timer.timeout.disconnect(this, GodotBossShootAtPlayerState::changeState)
    }

    @RegisterFunction
    fun changeState() {
        stateMachine.changeState(states["Idle"])
    }

}