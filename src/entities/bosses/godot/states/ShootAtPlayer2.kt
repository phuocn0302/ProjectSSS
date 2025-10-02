package entities.bosses.godot.states

import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween

@RegisterClass
class GodotBossShootAtPlayer2State : GodotBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 8.0

    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        boss.targetProjectileSpawner.active = true
        boss.patternProjectileSpawner.active = true

        durationTween = createTween()
        durationTween
            ?.tweenInterval(duration)
            ?.let {
                durationTween?.finished?.connect(this, GodotBossShootAtPlayer2State::changeState)
            }
    }

    @RegisterFunction
    override fun exit() {
        boss.targetProjectileSpawner.active = false
        boss.patternProjectileSpawner.active = false

        durationTween?.kill()
    }

    @RegisterFunction
    fun changeState() {
        stateMachine.changeState(states["DoublePunchAttack"])
    }

}