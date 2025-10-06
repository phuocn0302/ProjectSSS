package entities.bosses.godot.states

import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween

@RegisterClass
class GodotBossShootAtPlayerState : GodotBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 2.0

    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        boss.targetProjectileSpawner.active = true

        durationTween = createTween()
        durationTween
            ?.tweenInterval(duration)
            ?.let {
                durationTween?.finished?.connect(this, GodotBossShootAtPlayerState::changeState)
            }
    }

    @RegisterFunction
    override fun exit() {
        boss.targetProjectileSpawner.active = false

        durationTween?.kill()
    }

    @RegisterFunction
    fun changeState() {
        stateMachine.changeState(states["Idle"])
    }

}