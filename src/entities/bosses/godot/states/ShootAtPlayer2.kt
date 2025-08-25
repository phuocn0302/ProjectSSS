package entities.bosses.godot.states

import commons.singletons.Utils
import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.SceneTreeTimer

@RegisterClass
class GodotBossShootAtPlayer2State : GodotBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 8.0

    private lateinit var timer: SceneTreeTimer

    @RegisterFunction
    override fun enter() {
        boss.targetProjectileSpawner.active = true
        boss.patternProjectileSpawner.active = true

        timer = Utils.createTimer(this, duration)!!
        timer.timeout.connect(this, GodotBossShootAtPlayer2State::changeState)
    }

    @RegisterFunction
    override fun exit() {
        boss.targetProjectileSpawner.active = false
        boss.patternProjectileSpawner.active = false

        timer.timeout.disconnect(this, GodotBossShootAtPlayer2State::changeState)
    }

    @RegisterFunction
    fun changeState() {
        stateMachine.changeState(states["DoublePunchAttack"])
    }

}