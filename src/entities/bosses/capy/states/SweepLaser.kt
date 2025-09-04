package entities.bosses.capy.states

import commons.lasers.LaserData
import commons.singletons.Utils
import entities.bosses.capy.CapyBossState
import entities.projectiles.spawners.CircleProjectileSpawnerData
import godot.annotation.*
import godot.api.Tween
import godot.core.Vector2
import godot.core.connect
import godot.core.signal0
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class CapyBossSweepLaserState : CapyBossState() {

    @RegisterSignal
    val onMovedToEdge by signal0()

    @Export
    @RegisterProperty
    var maxNumberOfSweep: Int = 4

    @Export
    @RegisterProperty
    var minNumberOfSweep: Int = 2

    @Export
    @RegisterProperty
    var continueSweepChange: Double = 0.6

    @Export
    @RegisterProperty
    var sweepTime: Double = 1.0

    @Export
    @RegisterProperty
    var forwardOffset: Double = 20.0

    @Export
    @RegisterProperty
    var moveToEdgeTime: Double = 0.5

    @Export
    @RegisterProperty
    var delayBeforeSweep: Double = 1.0

    private var edgeLocation = variantArrayOf<Vector2>()
    private var locationIndex: Int = 0
    private var sweepCounter: Int = 0
    private var moveTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        with(boss) {
            gun.play("to_ray_gun")

            edgeLocation = variantArrayOf(
                Vector2(0, boss.globalPosition.y + forwardOffset),
                Vector2(180, boss.globalPosition.y + forwardOffset)
            )

            sweepCounter = 0

            setupCircleProjectileSpawner()
            setupLaserEmitter()

            attack()
        }
    }

    @RegisterFunction
    override fun exit() {
        boss.gun.play("back_to_gun")

        moveTween = this.createTween()
        moveTween?.tweenProperty(boss, "global_position", boss.defaultPosition, 0.5)
    }

    private fun attack() = godotCoroutine(context = GodotDispatchers.MainThread) {
        moveToEdge()
        onMovedToEdge.await()
        sweep()
    }

    private fun sweep() {
        godotCoroutine(context = GodotDispatchers.MainThread) {
            sweepCounter++
            locationIndex = GD.posmod(locationIndex + 1, 2)

            with(boss) {
                moveWarningLine.direction = boss.globalPosition.directionTo(edgeLocation[locationIndex])
                moveWarningLine.show()

                Utils.createTimer(this@CapyBossSweepLaserState, delayBeforeSweep)
                    ?.timeout
                    ?.await()

                moveWarningLine.hide()
                shoot()

                moveTween = this.createTween()
                moveTween
                    ?.tweenProperty(boss, "global_position", edgeLocation[locationIndex], sweepTime)
                    ?.finished
                    ?.connect(this@CapyBossSweepLaserState, CapyBossSweepLaserState::onSweepEnd)
            }
        }
    }

    @RegisterFunction
    fun onSweepEnd() = godotCoroutine(context = GodotDispatchers.MainThread) {
        boss.circleProjectileSpawner.active = false

        val continueSweep = if (sweepCounter < minNumberOfSweep) {
            true
        } else {
            GD.randf() < continueSweepChange
        }


        Utils.createTimer(this@CapyBossSweepLaserState, delayBeforeSweep)
            ?.timeout
            ?.await()

        if (continueSweep && sweepCounter < maxNumberOfSweep) {
            sweep()
        } else {
            stateMachine.changeState(states["ChaseLaser"])
        }

    }

    private fun shoot() {
        boss.circleProjectileSpawner.active = true
        boss.recoilGun()
        boss.laserEmitter.emitLaser()
    }

    private fun moveToEdge() {
        moveTween = this.createTween()
        moveTween?.let {
            it.setTrans(Tween.TransitionType.EXPO)
            it.setEase(Tween.EaseType.IN_OUT)

            it.tweenProperty(
                boss,
                "global_position",
                edgeLocation[locationIndex],
                moveToEdgeTime
            )

            it.finished.connect {
                onMovedToEdge.emit()
            }
        }
    }

    private fun setupLaserEmitter() {
        val laserData = LaserData()
        laserData.emitTime = sweepTime
        laserData.castTime = 0.0

        boss.laserEmitter.laserData = laserData
    }

    private fun setupCircleProjectileSpawner() {
        val spawnerData = CircleProjectileSpawnerData()

        spawnerData.numberOfProjectiles = 10
        spawnerData.rotatePattern = true
        spawnerData.rotateSpeed = 10.0
        spawnerData.spawnInterval = 0.1

        boss.circleProjectileSpawner.spawnerData = spawnerData
    }
}