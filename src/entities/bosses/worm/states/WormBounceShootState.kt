package entities.bosses.worm.states

import commons.components.movements.IdleMoveComponent
import entities.bosses.worm.WormBossState
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.GPUParticles2D
import godot.api.Tween
import godot.common.util.PI
import godot.extension.getNodeAs

@RegisterClass
class WormBounceShootState : WormBossState() {

    @Export
    @RegisterProperty
    var chargeTime: Double = 1.0

    @Export
    @RegisterProperty
    var duration: Double = 1.0

    private var isCharging: Boolean = false
    private var chargeTween: Tween? = null
    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        isCharging = true

        boss.idleMoveComponent.active = false
        boss.bounceShootChargeParticles.emitting = true

        chargeTween = createTween()
        chargeTween?.tweenInterval(chargeTime)
        chargeTween?.finished?.connect(this, WormBounceShootState::startShooting)
    }

    @RegisterFunction
    override fun exit() {
        chargeTween?.kill()
        durationTween?.kill()
        boss.bounceProjSpawner.active = false
        boss.idleMoveComponent.active = true
        boss.bounceShootChargeParticles.emitting = false
    }

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (!isCharging || boss.player == null) return
        boss.globalRotation =
            (boss.globalPosition.directionTo(boss.player!!.globalPosition).angle() - PI / 2).toFloat()
    }

    @RegisterFunction
    fun startShooting() {
        isCharging = false
        boss.bounceProjSpawner.active = true
        durationTween = createTween()
        durationTween?.tweenInterval(duration)
        durationTween?.finished?.connect(this, WormBounceShootState::onDurationEnd)

        createTween()?.tweenProperty(boss, "global_rotation", 0, duration)
    }

    @RegisterFunction
    fun onDurationEnd() {
        stateMachine.changeState(states["Idle"])
    }
}



