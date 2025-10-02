package entities.bosses.capy.states

import commons.lasers.LaserData
import entities.bosses.capy.CapyBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
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

    private var preShootTween: Tween? = null
    private var shootTimerTween: Tween? = null
    private var postLaserTween: Tween? = null
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

            isShooting = false

            preShootTween = this@CapyBossChaseLaserState.createTween()
            preShootTween
                ?.tweenInterval(chaseDuration * 0.9)
                ?.let {
                    preShootTween?.finished?.connect {
                        isShooting = true
                        warningLine.hide()
                    }
                }

            shootTimerTween = this@CapyBossChaseLaserState.createTween()
            shootTimerTween
                ?.tweenInterval(chaseDuration)
                ?.let {
                    shootTimerTween?.finished?.connect(this@CapyBossChaseLaserState, CapyBossChaseLaserState::shoot)
                }
        }
    }

    @RegisterFunction
    override fun exit() {
        with(boss) {
            gun.play("back_to_gun")
            warningLine.hide()

            leftMissileSpawner.active = false
            rightMissileSpawner.active = false

            this.createTween()
                ?.tweenProperty(boss, "global_position", defaultPosition, 0.5)
        }
        preShootTween?.kill()
        shootTimerTween?.kill()
        postLaserTween?.kill()
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        with(boss) {
            player?.let { player ->
                if (!warningLine.visible) {
                    warningLine.show()
                }

                // preShootTween handles switching to shooting state and hiding warning

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
            postLaserTween = this@CapyBossChaseLaserState.createTween()
            postLaserTween
                ?.tweenInterval(totalLaserTime)
                ?.let {
                    postLaserTween?.finished?.connect {
                        isShooting = false
                        shotCounter++

                        if (shotCounter >= numberOfShot) {
                            stateMachine.changeState(states["Idle"])
                            return@connect
                        }

                        isShooting = false

                        preShootTween = this@CapyBossChaseLaserState.createTween()
                        preShootTween
                            ?.tweenInterval(chaseDuration * 0.9)
                            ?.let {
                                preShootTween?.finished?.connect {
                                    isShooting = true
                                    warningLine.hide()
                                }
                            }

                        shootTimerTween = this@CapyBossChaseLaserState.createTween()
                        shootTimerTween
                            ?.tweenInterval(chaseDuration)
                            ?.let {
                                shootTimerTween?.finished?.connect(this@CapyBossChaseLaserState, CapyBossChaseLaserState::shoot)
                            }
                    }
                }
        }
    }

    private fun setupLaserEmitter() {
        boss.laserEmitter.laserData = laserData
    }
}
