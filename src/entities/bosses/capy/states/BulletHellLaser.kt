package entities.bosses.capy.states

import commons.lasers.LaserData
import entities.bosses.capy.CapyBossState
import entities.projectiles.spawners.CircleProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
import godot.core.connect
import godot.global.GD

@RegisterClass
class CapyBossBulletHellLaserState : CapyBossState() {

    @Export
    @RegisterProperty
    var duration: Double = 7.0

    @Export
    @RegisterProperty
    var shootDelay: Double = 1.0

    private var shootDelayTween: Tween? = null
    private var durationTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        with(boss) {
            idleMoveComponent.active = false

            warningLine.show()

            gun.play("to_ray_gun")

            setupCircleProjectileSpawner()
            setupLaserEmitter()

            shootDelayTween = this@CapyBossBulletHellLaserState.createTween()
            shootDelayTween
                ?.tweenInterval(shootDelay)
                ?.let {
                    shootDelayTween?.finished?.connect {
                        startShooting()
                    }
                }

        }
    }

    @RegisterFunction
    override fun exit() {
        with(boss) {
            gun.play("back_to_gun")

            warningLine.hide()
            laserEmitter.forceStopEmit()
            circleProjectileSpawner.active = false
            idleMoveComponent.active = true

            this.createTween()
                ?.tweenProperty(
                    boss,
                    "global_position",
                    defaultPosition,
                    0.5
                )
        }
        shootDelayTween?.kill()
        durationTween?.kill()
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        boss.player?.let { player ->
            val pos = boss.globalPosition
            pos.x = GD.lerp(pos.x, player.globalPosition.x, delta)
            boss.globalPosition = pos
        }
    }

    private fun startShooting() {
        with(boss) {
            recoilGun()

            laserEmitter.emitLaser()
            circleProjectileSpawner.active = true

            durationTween = this@CapyBossBulletHellLaserState.createTween()
            durationTween
                ?.tweenInterval(duration)
                ?.let {
                    durationTween?.finished?.connect(this@CapyBossBulletHellLaserState, CapyBossBulletHellLaserState::onTimerTimeout)
                }
        }

    }

    private fun setupLaserEmitter() {
        val laserData = LaserData()
        laserData.emitTime = duration
        laserData.castTime = 0.5

        boss.laserEmitter.laserData = laserData
    }

    private fun setupCircleProjectileSpawner() {
        val spawnerData = CircleProjectileSpawnerData()

        spawnerData.numberOfProjectiles = 1
        spawnerData.rotatePattern = true
        spawnerData.rotateSpeed = 15.0
        spawnerData.spawnInterval = 0.05

        boss.circleProjectileSpawner.spawnerData = spawnerData
    }

    @RegisterFunction
    fun onTimerTimeout() {
        stateMachine.changeState(states["Idle"])
    }
}