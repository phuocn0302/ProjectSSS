package entities.bosses.godot.states

import commons.singletons.Utils
import entities.bosses.godot.GodotBoss
import entities.enemies.common_states.EnemyDieState
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.api.PackedScene
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD.load

@RegisterClass
class GodotBossDieState : EnemyDieState() {

    companion object {
        val CIRCLE_EXPLOSION = load<PackedScene>("res://src/particles/circle_explosion.tscn")
    }

    lateinit var boss: GodotBoss

    @RegisterFunction
    override fun enter() = godotCoroutine(context = GodotDispatchers.MainThread) {
        super.enter()

        boss = getNodeAs("../..")!!

        boss.leftArm.selfDestruct()
        boss.rightArm.selfDestruct()

        boss.rightArm.onDestroyCompleted.await()

        val tween1 = createTween()
        tween1?.tweenInterval(1.0)
        tween1?.finished?.await()

        val vfx = CIRCLE_EXPLOSION!!.instantiate() as Node2D
        vfx.globalPosition = boss.globalPosition
        getTree()?.currentScene?.addChild(vfx)

        boss.animatedSprite2D.hide()

        val tween2 = createTween()
        tween2?.tweenInterval(1.0)
        tween2?.finished?.await()

        boss.eyeParticles.emitting = false

        val tween3 = createTween()
        tween3?.tweenInterval(1.0)
        tween3?.finished?.await()

        boss.queueFree()
    }
}