package entities.bosses.capy.states

import entities.bosses.capy.CapyBossState
import entities.projectiles.ProjectileSpawner
import entities.projectiles.spawners.CircleProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class CapyBossCircleShootState : CapyBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 2.0

    @Export
    @RegisterProperty
    var burstTime: Double = 0.5

    @Export
    @RegisterProperty
    var burstDelay: Double = 0.5

    private var missileSpawners = variantArrayOf<ProjectileSpawner>()

    private var durationTween: Tween? = null
    private var burstTween: Tween? = null
    private var burstDelayTween: Tween? = null
    private var missileSpawnerIndex: Int = 0

    @RegisterFunction
    override fun enter() {

        setupCircleProjectileSpawner()
        missileSpawners = variantArrayOf(boss.leftMissileSpawner, boss.rightMissileSpawner)

        durationTween = this.createTween()
        durationTween
            ?.tweenInterval(duration)
            ?.let {
                durationTween?.finished?.connect(this, CapyBossCircleShootState::onTimerTimeout)
            }

        shoot()
    }

    @RegisterFunction
    override fun exit() {
        boss.circleProjectileSpawner.active = false
        durationTween?.kill()
        burstTween?.kill()
        burstDelayTween?.kill()
    }

    @RegisterFunction
    fun shoot() = godotCoroutine(context = GodotDispatchers.MainThread) {
        missileSpawners[missileSpawnerIndex].spawn()
        missileSpawnerIndex = GD.posmod(missileSpawnerIndex + 1, 2)

        boss.circleProjectileSpawner.active = true
        (boss.circleProjectileSpawner.spawnerData as CircleProjectileSpawnerData).rotateSpeed *= -1

        burstTween = this@CapyBossCircleShootState.createTween()
        burstTween?.tweenInterval(burstTime)
        burstTween?.finished?.await()

        if (!boss.circleProjectileSpawner.active) {
            return@godotCoroutine
        }

        boss.circleProjectileSpawner.active = false

        burstDelayTween = this@CapyBossCircleShootState.createTween()
        burstDelayTween
            ?.tweenInterval(burstDelay)
            ?.let {
                burstDelayTween?.finished?.connect(this@CapyBossCircleShootState, CapyBossCircleShootState::shoot)
            }

    }

    private fun setupCircleProjectileSpawner() {
        val spawnerData = CircleProjectileSpawnerData()

        spawnerData.numberOfProjectiles = 15
        spawnerData.rotatePattern = true
        spawnerData.rotateSpeed = 10.0
        spawnerData.spawnInterval = 0.2

        boss.circleProjectileSpawner.spawnerData = spawnerData
    }

    @RegisterFunction
    fun onTimerTimeout() {
        boss.circleProjectileSpawner.active = false
        stateMachine.changeState(states["Idle"])
    }
}