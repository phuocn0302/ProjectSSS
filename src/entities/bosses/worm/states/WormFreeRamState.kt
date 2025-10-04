package entities.bosses.worm.states

import commons.singletons.Utils
import entities.bosses.worm.WormBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
import godot.common.util.PI
import godot.core.Callable
import godot.core.Vector2
import godot.core.connect
import godot.core.toGodotName
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class WormFreeRamState : WormBossState() {

    @Export
    @RegisterProperty
    var numberOfRam: Int = 5

    @Export
    @RegisterProperty
    var speed: Double = 300.0

    @Export
    @RegisterProperty
    var goUpDuration: Double = 1.0

    @Export
    @RegisterProperty
    var goingBackPos: Vector2 = Vector2(90, 0)

    private var tween: Tween? = null
    private var ramCounter: Int = 0
    private var isRamming: Boolean = false
    private var isGoingToStartPos: Boolean = false
    private var currentSpeed: Double = 0.0
    private var isRamFromLeft: Boolean = true
    private var ramFromPos: Vector2 = Vector2.ZERO
    private var ramToPos: Vector2 = Vector2.ZERO
    private var direction: Vector2 = Vector2.ZERO
    private var warningLine: commons.MoveWarningLine? = null

    @RegisterFunction
    override fun enter() {
        GD.randomize()
        ramCounter = 0
        boss.idleMoveComponent.active = false

        if (warningLine == null) {
            warningLine = commons.MoveWarningLine()
            warningLine?.addToGroup("warning_line")
            getTree()?.currentScene?.addChild(warningLine!!)
        }

//        boss.segments[0].shootTrigger.screenEntered.connect(this, WormFreeRamState::shoot)

        goUp()
        godotCoroutine(context = godot.coroutines.GodotDispatchers.MainThread) {
            tween?.finished?.await()
            attack()
        }
    }

    @RegisterFunction
    override fun exit() {
        isRamming = false
        isGoingToStartPos = false
        boss.idleMoveComponent.active = true
        tween?.kill()
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        if (isRamming) {
            currentSpeed += 100.0 * delta
            boss.globalPosition += direction * currentSpeed * delta
        } else if (isGoingToStartPos) {
            moveToStartPos(delta)
        }
    }

    private fun moveToStartPos(delta: Double) {
        val targetDir = boss.globalPosition.directionTo(ramFromPos)
        val angleToTarget = direction.angleTo(targetDir)
        val maxAngle = GD.degToRad(80.0) * delta
        val clamped = GD.clamp(angleToTarget, -maxAngle, maxAngle)
        direction = direction.rotated(clamped).normalized()

        if (boss.globalPosition.distanceSquaredTo(ramFromPos) > 100) {
            boss.globalPosition += direction * speed * delta
            boss.globalRotation = (direction.angle() - PI / 2).toFloat()
        } else {
            isGoingToStartPos = false
        }
    }

    private fun attack() {
        ramCounter += 1

        makePath()
        direction = ramFromPos.directionTo(ramToPos)
        boss.globalRotation = (direction.angle() - PI / 2).toFloat()
        boss.globalPosition = ramFromPos + (-direction * 200.0)

        warningLine?.show()

        currentSpeed = speed
        val waitTween = Utils.createTweenTimer(this, 1.0)
        godotCoroutine(context = godot.coroutines.GodotDispatchers.MainThread) {
            waitTween?.finished?.await()

            warningLine?.hide()
            isRamming = true
            boss.screenShakeComponent.shake()

            val runTween = Utils.createTweenTimer(this@WormFreeRamState, 3.0)
            runTween?.finished?.connect {
                isRamming = false

                if (ramCounter < numberOfRam) {
                    attack()
                } else {
                    boss.segments[0].shootTrigger.screenEntered.disconnect(this@WormFreeRamState, WormFreeRamState::shoot)
                    tween = createTween()?.setParallel(true)
                    tween?.tweenProperty(boss, "global_rotation", 0, 0.5)
                    tween?.tweenProperty(boss, "global_position", Vector2(90, -50), 1.0)
                    tween?.chain()
                    tween?.tweenProperty(boss, "global_position", boss.defaultPosition, 1.0)
                    tween?.finished?.connect { stateMachine.changeState(states["Idle"]) }
                }
            }
        }
    }

    private fun goUp() {
        val goUpOffset = boss.globalPosition + Vector2.UP * 200.0
        tween = createTween()
        tween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)
        tween?.tweenProperty(boss, "global_position", goUpOffset, goUpDuration)
    }

    private fun makePath() {
        ramFromPos = Vector2(0, GD.randiRange(0, 256).toDouble())
        ramToPos = Vector2(0, GD.randiRange(0, 256).toDouble())

        if (isRamFromLeft) {
            ramToPos.x = 180.0
        } else {
            ramFromPos.x = 180.0
        }

        boss.player?.let { player ->
            if (GD.randf() >= 0.5) {
                ramToPos.y = player.globalPosition.y
            }
        }

        isRamFromLeft = !isRamFromLeft

        warningLine?.globalPosition = ramFromPos
        warningLine?.direction = ramFromPos.directionTo(ramToPos)
    }

    @RegisterFunction
    fun shoot() {
        val endTime = godot.api.Time.getTicksMsec() + 2000
        godotCoroutine(context = godot.coroutines.GodotDispatchers.MainThread) {
            while (godot.api.Time.getTicksMsec() < endTime) {
//                boss.segments.forEach { it.targetProjectileSpawner.spawn() }
                val wait = Utils.createTweenTimer(this@WormFreeRamState, 0.3)
                wait?.finished?.await()
            }
        }
    }
}



