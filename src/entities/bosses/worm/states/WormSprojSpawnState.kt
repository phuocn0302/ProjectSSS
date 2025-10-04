package entities.bosses.worm.states

import commons.singletons.Utils
import entities.bosses.worm.WormBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween

@RegisterClass
class WormSprojSpawnState : WormBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 6.0

    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        boss.targetProjectileSpawner.active = true

        durationTween = createTween()
        durationTween?.tweenInterval(duration)
        durationTween?.finished?.connect(this, WormSprojSpawnState::onTimeout)
    }

    @RegisterFunction
    override fun exit() {
        durationTween?.kill()
        boss.targetProjectileSpawner.active = false
    }

    @RegisterFunction
    fun onTimeout() {
        stateMachine.changeState(states["Idle"])
    }
}
