package entities.bosses.capy.states

import commons.singletons.Utils
import entities.bosses.capy.CapyBossState
import entities.projectiles.spawners.ProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class CapyBossMissileShootState : CapyBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 2.0

    @Export
    @RegisterProperty
    var missileIntervalRange = Vector2(2, 3)

    @RegisterFunction
    override fun enter() {
        with(boss) {

            leftMissileSpawner.setInterval(
                GD.randfRange(
                    missileIntervalRange.x.toFloat(),
                    missileIntervalRange.y.toFloat()
                ).toDouble()
            )
            rightMissileSpawner.setInterval(
                GD.randfRange(
                    missileIntervalRange.x.toFloat(),
                    missileIntervalRange.y.toFloat()
                ).toDouble()
            )

            leftMissileSpawner.active = true
            rightMissileSpawner.active = true
            patternProjectileSpawner.active = true

            Utils.createTimer(this@CapyBossMissileShootState, duration)
                ?.timeout
                ?.connect(
                    this@CapyBossMissileShootState,
                    CapyBossMissileShootState::onTimerTimeout
                )
        }
    }

    @RegisterFunction
    fun onTimerTimeout() {
        with(boss) {
            leftMissileSpawner.active = false
            rightMissileSpawner.active = false
            patternProjectileSpawner.active = false

            stateMachine.changeState(states["Idle"])
        }
    }

}