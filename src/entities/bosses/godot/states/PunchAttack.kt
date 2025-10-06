package entities.bosses.godot.states

import entities.bosses.godot.GodotBossArm
import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Tween
import godot.core.Callable
import godot.core.Vector2
import godot.core.toGodotName
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class GodotBossPunchAttackState : GodotBossState() {

    @Export
    @RegisterProperty
    var followSpeed: Double = 120.0

    @Export
    @RegisterProperty
    var playerOffset: Double = 100.0

    @Export
    @RegisterProperty
    var attackDelay: Double = 3.0

    @Export
    @RegisterProperty
    var windupTime: Double = 0.4

    @Export
    @RegisterProperty
    var windupOffset: Double = 30.0

    @Export
    @RegisterProperty
    var punchTime: Double = 0.2

    @Export
    @RegisterProperty
    var armReturnTime: Double = 1.0

    @Export
    @RegisterProperty
    var punchDistance: Double = 150.0

    private lateinit var attackArm: GodotBossArm
    private var armIndex: Int = 0
    private lateinit var armOrgPos: Vector2
    private var attackDelayTween: Tween? = null
    private var tween: Tween? = null
    private var following: Boolean = false

    @RegisterFunction
    override fun enter() {
        with(boss) {
            circleProjectileSpawner.active = true

            armIndex = GD.posmod(armIndex + 1, 2)
            attackArm = variantArrayOf<GodotBossArm>(boss.leftArm, boss.rightArm)[armIndex]

            armOrgPos = variantArrayOf(
                leftArmOrgPos.globalPosition,
                rightArmOrgPos.globalPosition
            )[armIndex]
        }

        following = true
        attackDelayTween = createTween()
        attackDelayTween
            ?.tweenInterval(attackDelay)
            ?.let {
                attackDelayTween?.finished?.connect(this, GodotBossPunchAttackState::attack)
            }

        tween?.kill()
    }

    @RegisterFunction
    override fun exit() {
        boss.circleProjectileSpawner.active = false
        following = false
        attackDelayTween?.kill()
        tween?.kill()
    }

    @RegisterFunction
    override fun processFrame(delta: Double) {
        if (following) {
            followPlayer(delta)
        }
    }

    @RegisterFunction
    fun attack() = godotCoroutine(context = GodotDispatchers.MainThread) {
        attackArm.ghostTrailingComponent.active = true

        windUp()
        tween?.finished?.await()

        punch()
        tween?.finished?.await()
        attackArm.explode()

        val delayTween = createTween()
        delayTween?.tweenInterval(1.0)
        delayTween?.finished?.await()

        goBack()
        tween?.finished?.await()

        attackArm.ghostTrailingComponent.active = false
        stateMachine.changeState(states["Idle"])
    }

    private fun windUp() {
        val windupPos = attackArm.position + Vector2.UP * windupOffset

        tween = createTween()
        tween?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)

        tween?.tweenProperty(attackArm, "position", windupPos, windupTime)
    }

    private fun punch() {
        val punchPos = attackArm.position+ Vector2.DOWN * punchDistance

        tween = createTween()
        tween?.setTrans(Tween.TransitionType.BACK)?.setEase(Tween.EaseType.IN)

        tween?.tweenProperty(attackArm, "position", punchPos, punchTime)
    }

    private fun goBack() {
        tween = createTween()
        tween?.tweenProperty(attackArm, "global_position", armOrgPos, armReturnTime)
    }

    private fun followPlayer(delta: Double) {
        boss.player?.let { player ->
            val offset = Vector2(
                player.globalPosition.x,
                player.globalPosition.y - playerOffset
            )

            attackArm.globalPosition = attackArm.globalPosition.moveToward(
                offset,
                followSpeed * delta
            )
        }
    }
}