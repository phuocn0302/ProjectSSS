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
class WormCircleShootState : WormBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 3.0

    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        boss.circleProjectileSpawner.active = true

        durationTween = createTween()
        durationTween?.tweenInterval(duration)
        durationTween?.finished?.connect(this, WormCircleShootState::onTimeout)
    }

    @RegisterFunction
    override fun exit() {
        durationTween?.kill()
        boss.circleProjectileSpawner.active = false
    }

    @RegisterFunction
    fun onTimeout() {
        stateMachine.changeState(states["Idle"])
    }
}



