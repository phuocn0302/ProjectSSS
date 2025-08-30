package entities.bosses.capy.states

import commons.lasers.LaserData
import commons.singletons.Utils
import entities.bosses.capy.CapyBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.SceneTreeTimer
import godot.core.connect
import godot.global.GD

@RegisterClass
class CapyBossChaseLaserState : CapyBossState() {

    @Export
    @RegisterProperty
    var numberOfShot: Int = 4

    @Export
    @RegisterProperty
    var chaseDuration: Double = 3.0

    @Export
    @RegisterProperty
    var moveSpeed: Double = 15.0

    @Export
    @RegisterProperty
    var laserData = LaserData()

    private var shootTimer: SceneTreeTimer? = null
    private var isShooting: Boolean = false
    private var shotCounter: Int = 0

    @RegisterFunction
    override fun enter() {
        with(boss) {
            gun.play("to_ray_gun")

            leftMissileSpawner.setInterval(3.0)
            rightMissileSpawner.setInterval(3.0)

            leftMissileSpawner.active = true
            rightMissileSpawner.active = true

            warningLine.show()

            setupLaserEmitter()

            shootTimer = Utils.createTimer(this@CapyBossChaseLaserState, chaseDuration)
            shootTimer?.timeout?.connect(this@CapyBossChaseLaserState, CapyBossChaseLaserState::shoot)
        }
    }

    @RegisterFunction
    override fun exit() {
        with(boss) {
            gun.play("back_to_gun")
            warningLine.hide()

            leftMissileSpawner.active = false
            rightMissileSpawner.active = false

            shootTimer?.timeout?.disconnect(this@CapyBossChaseLaserState, CapyBossChaseLaserState::shoot)

            createTween()
                ?.tweenProperty(boss, "global_position", defaultPosition, 0.5)
        }
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        with(boss) {
            player?.let { player ->
                if (!warningLine.visible) {
                    warningLine.show()
                }

                shootTimer?.let {
                    if (it.timeLeft < chaseDuration * 0.1) {
                        isShooting = true
                        warningLine.hide()
                    }
                }

                val speed = if (isShooting) {
                    (moveSpeed / 8)
                } else {
                    moveSpeed
                }

                val pos = boss.globalPosition
                pos.x = GD.lerp(pos.x, player.globalPosition.x, speed * delta)

                boss.globalPosition = pos
            }
        }
    }

    @RegisterFunction
    fun shoot() {
        with(boss) {
            recoilGun()
            laserEmitter.emitLaser()

            val totalLaserTime = laserData.castTime + laserData.emitTime + laserData.decayTime
            Utils.createTimer(this@CapyBossChaseLaserState, totalLaserTime)
                ?.timeout
                ?.connect {
                    isShooting = false
                    shotCounter++

                    if (shotCounter >= numberOfShot) {
                        stateMachine.changeState(states["Idle"])
                        return@connect
                    }

                    shootTimer = Utils.createTimer(this@CapyBossChaseLaserState, chaseDuration)
                    shootTimer?.timeout?.connect(this@CapyBossChaseLaserState, CapyBossChaseLaserState::shoot)
                }
        }
    }

    private fun setupLaserEmitter() {
        boss.laserEmitter.laserData = laserData
    }
}
