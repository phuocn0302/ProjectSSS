package entities.bosses.worm.states

import entities.bosses.worm.WormBossState
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
import godot.extension.getNodeAs

@RegisterClass
class WormPatternShootState : WormBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 3.0

    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        boss.patternProjectileSpawner.active = true

        durationTween = createTween()
        durationTween?.tweenInterval(duration)
        durationTween?.finished?.connect(this, WormPatternShootState::onTimeout)
    }

    @RegisterFunction
    override fun exit() {
        durationTween?.kill()
        boss.patternProjectileSpawner.active = false
    }

    @RegisterFunction
    fun onTimeout() {
        stateMachine.changeState(states["Idle"])
    }
}



