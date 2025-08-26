package entities.bosses.godot.states

import commons.singletons.Utils
import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.SceneTreeTimer
import godot.api.Tween
import godot.core.Vector2
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class GodotBossDoublePunchAttackState : GodotBossState() {

    @Export
    @RegisterProperty
    var followSpeed: Double = 120.0

    @Export
    @RegisterProperty
    var playerOffset: Double = 80.0

    @Export
    @RegisterProperty
    var attackDelay: Double = 3.0

    @Export
    @RegisterProperty
    var windupTime: Double = 0.4

    @Export
    @RegisterProperty
    var punchTime: Double = 0.2

    @Export
    @RegisterProperty
    var armReturnTime: Double = 1.0

    @Export
    @RegisterProperty
    var punchDistance: Double = 150.0

    @Export
    @RegisterProperty
    var windUpOffset: Double = 30.0

    private var rightArmAttackPosIndex: Int = 0
    private lateinit var timer: SceneTreeTimer
    private var leftTween: Tween? = null
    private var rightTween: Tween? = null


    @RegisterFunction
    override fun enter() {
        boss.circleProjectileSpawner.active = true
        rightArmAttackPosIndex = GD.posmod(rightArmAttackPosIndex + 1, 2)
        timer = Utils.createTimer(this, attackDelay)!!
        timer.timeout.connect(this, GodotBossDoublePunchAttackState::attack)

        leftTween?.kill()
        rightTween?.kill()
    }

    @RegisterFunction
    override fun exit() {
        boss.circleProjectileSpawner.active = false
        boss.rightArm.rotation = 0.0F
        boss.rightArm.scale = Vector2.ONE
        timer.timeout.disconnect(this, GodotBossDoublePunchAttackState::attack)

        leftTween?.kill()
        rightTween?.kill()
    }

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (timer.timeLeft > 0) {
            followPlayer(delta)
        }
    }

    @RegisterFunction
    fun attack() = godotCoroutine(context = GodotDispatchers.MainThread) {
        boss.leftArm.ghostTrailingComponent.active = true

        rotateRightArm()

        windUpLeft()
        windUpRight()

        leftTween?.finished?.await()

        punchLeft()
        punchRight()
        leftTween?.finished?.await()

        boss.leftArm.explode()
        boss.rightArm.explode()

        Utils.createTimer(this@GodotBossDoublePunchAttackState, 0.5)
            ?.timeout
            ?.await()

        goBackLeft()
        goBackRight()

        leftTween?.finished?.await()

        boss.leftArm.ghostTrailingComponent.active = false
        stateMachine.changeState(states["ShootAtPlayer2"])
    }

    private fun followPlayer(delta: Double) {
        boss.player?.let { player ->
            val leftOffset = Vector2(
                player.globalPosition.x,
                player.globalPosition.y - playerOffset
            )

            val rightOffset = arrayOf(
                Vector2(
                    player.globalPosition.x + playerOffset,
                    player.globalPosition.y
                ),
                Vector2(
                    player.globalPosition.x - playerOffset,
                    player.globalPosition.y
                )
            )[rightArmAttackPosIndex]

            boss.leftArm.globalPosition = boss.leftArm.globalPosition.moveToward(leftOffset, followSpeed * delta)
            boss.rightArm.globalPosition = boss.rightArm.globalPosition.moveToward(rightOffset, followSpeed * delta)
        }
    }

    private fun rotateRightArm() {
        val tween = createTween()?.setParallel()
        if (rightArmAttackPosIndex == 0) {
            boss.rightArm.scale = Vector2(1.0, -1.0)
        }
        tween?.tweenProperty(boss.rightArm, "rotation", -Math.PI / 2, windupTime)
    }

    private fun windUpLeft() {
        val windupPos = boss.leftArm.position + Vector2.UP * 30.0

        leftTween = createTween()
        leftTween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)

        leftTween?.tweenProperty(boss.leftArm, "position", windupPos, windupTime)
    }

    private fun punchLeft() {
        val punchPos = boss.leftArm.position + Vector2.DOWN * punchDistance
        leftTween = createTween()
        leftTween?.setTrans(Tween.TransitionType.BACK)?.setEase(Tween.EaseType.IN)

        leftTween?.tweenProperty(boss.leftArm, "position", punchPos, punchTime)
    }

    private fun goBackLeft() {
        leftTween = createTween()
        leftTween?.tweenProperty(boss.leftArm, "global_position", boss.leftArmOrgPos.globalPosition, armReturnTime)
    }

    private fun windUpRight() {
        var offset = Vector2.RIGHT * windUpOffset
        if (rightArmAttackPosIndex == 1) {
            offset = Vector2.LEFT * windUpOffset
        }

        val windupOffset = boss.rightArm.position + offset

        rightTween = createTween()
        rightTween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)

        rightTween?.tweenProperty(boss.rightArm, "position", windupOffset, windupTime)
    }

    private fun punchRight() {
        var punchOffset = Vector2.LEFT * punchDistance
        if (rightArmAttackPosIndex == 1) {
            punchOffset = Vector2.RIGHT * punchDistance
        }

        val punchPos = boss.rightArm.position + punchOffset

        rightTween = createTween()
        rightTween?.setTrans(Tween.TransitionType.BACK)?.setEase(Tween.EaseType.IN)
        rightTween?.tweenProperty(boss.rightArm, "position", punchPos, punchTime)
    }

    private fun goBackRight() {
        rightTween = createTween()
        rightTween?.tweenProperty(boss.rightArm, "global_position", boss.rightArmOrgPos.globalPosition, armReturnTime)
    }
}
