package entities.bosses.worm.states

import commons.MoveWarningLine
import commons.singletons.Utils
import entities.bosses.worm.WormBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.Tween
import godot.common.util.PI
import godot.core.Vector2
import godot.core.connect
import godot.core.signal0
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class WormRamState : WormBossState() {

    @RegisterSignal
    val wentBack by signal0()

    @Export
    @RegisterProperty
    var windUpAmount: Double = 30.0

    @Export
    @RegisterProperty
    var windUpDuration: Double = 1.0

    @Export
    @RegisterProperty
    var ramDistance: Double = 400.0

    @Export
    @RegisterProperty
    var ramDuration: Double = 0.5

    @Export
    @RegisterProperty
    var ramDelay: Double = 0.6

    @Export
    @RegisterProperty
    var goingBackPos: Vector2 = Vector2(90, 0)

    private var tween: Tween? = null
    private var dirToPlayer: Vector2 = Vector2.DOWN
    private var ramToPos: Vector2 = Vector2.ZERO
    private var isRamming: Boolean = false
    private var isGoingBack: Boolean = false
    private var isAttackDirSet: Boolean = false
    private var attackDir: Vector2 = Vector2.DOWN
    private var direction: Vector2 = Vector2.ZERO
    private var warningLine: MoveWarningLine? = null

    private var windupTween: Tween? = null
    private var ramTween: Tween? = null
    private var backTween: Tween? = null
    private var delayTween: Tween? = null

    @RegisterFunction
    override fun enter() {
        attackDir = Vector2.DOWN
        boss.idleMoveComponent.active = false

        if (warningLine == null) {
            warningLine = MoveWarningLine()
            warningLine?.addToGroup("warning_line")
            getTree()?.currentScene?.addChild(warningLine!!)
        }

        attack()
    }

    @RegisterFunction
    override fun exit() {
        isRamming = false
        isGoingBack = false
        isAttackDirSet = false
        boss.idleMoveComponent.active = true

        tween?.kill()
        windupTween?.kill()
        ramTween?.kill()
        backTween?.kill()
        delayTween?.kill()
    }

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (boss.player == null) {
            dirToPlayer = Vector2.DOWN
            return
        }

        dirToPlayer = boss.globalPosition.directionTo(boss.player!!.globalPosition)

        if (!isAttackDirSet) {
            boss.globalRotation = (dirToPlayer.angle() - PI / 2).toFloat()
        }
    }

    @RegisterFunction
    override fun processPhysics(delta: Double) {
        if (!isGoingBack) return

        val targetDir = boss.globalPosition.directionTo(goingBackPos)
        val angleToTarget = direction.angleTo(targetDir)

        val maxAngle = GD.degToRad(80.0) * delta
        val clampedAngle = GD.clamp(angleToTarget, -maxAngle, maxAngle)

        direction = direction.rotated(clampedAngle).normalized()

        if (boss.globalPosition.distanceSquaredTo(goingBackPos) > 100) {
            boss.globalPosition += direction * 500.0 * delta
            boss.globalRotation = (direction.angle() - PI / 2).toFloat()
        } else {
            isGoingBack = false
            wentBack.emit()
        }
    }

    private fun attack() = godotCoroutine(context = GodotDispatchers.MainThread) {
        windUp()
        windupTween?.finished?.await()
        delayTween?.finished?.await()
        ram()
        ramTween?.finished?.await()
        isGoingBack = true
        wentBack.await()

        tween = createTween()?.setParallel(true)
        tween?.tweenProperty(boss, "global_rotation", 0, 0.5)
        tween?.tweenProperty(boss, "global_position", boss.defaultPosition, 1.0)
        tween?.finished?.await()

        stateMachine.changeState(states["Idle"])
    }

    private fun windUp() {
        boss.screenShakeComponent.shake()
        val windUpOffset = boss.globalPosition + Vector2.UP * windUpAmount

        windupTween = createTween()
        windupTween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)
        windupTween?.tweenProperty(boss, "global_position", windUpOffset, windUpDuration)
        windupTween?.finished?.connect {
            warningLine?.globalPosition = boss.globalPosition
            warningLine?.direction = dirToPlayer
            warningLine?.showTime = 0.2
            warningLine?.show()

            attackDir = dirToPlayer
            isAttackDirSet = true

            delayTween = Utils.createTweenTimer(this@WormRamState, ramDelay)
        }
    }

    private fun ram() {
        boss.screenShakeComponent.shake()

        isRamming = true
        ramToPos = boss.globalPosition + attackDir * ramDistance
        direction = attackDir

        ramTween = createTween()
        ramTween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)
        ramTween?.tweenProperty(boss, "global_position", ramToPos, ramDuration)

        warningLine?.hide()
//        boss.segments.forEach { it.circleProjectileSpawner.spawn() }
    }
}
