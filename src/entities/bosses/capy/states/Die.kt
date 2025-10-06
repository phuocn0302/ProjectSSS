package entities.bosses.capy.states

import commons.singletons.Utils
import entities.bosses.capy.CapyBoss
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
class CapyBossDieState : EnemyDieState() {

    val CIRCLE_EXPLOSION = load<PackedScene>("res://src/particles/circle_explosion.tscn")

    lateinit var boss: CapyBoss

    @RegisterFunction
    override fun enter() = godotCoroutine(context = GodotDispatchers.MainThread) {
        boss = getNodeAs("../..")!!

        super.enter()

        val tween1 = createTween()
        tween1?.tweenInterval(3.0)
        tween1?.finished?.await()

        CIRCLE_EXPLOSION?.let {
            val vfx = it.instantiate() as Node2D
            vfx.globalPosition = boss.globalPosition

            getTree()?.currentScene?.addChild(vfx)
        }

        boss.gun.hide()
        boss.mainSprite.hide()

        val tween2 = createTween()
        tween2?.tweenInterval(1.0)
        tween2?.finished?.await()

        boss.gpuParticles2D.emitting = false

        val tween3 = createTween()
        tween3?.tweenInterval(1.0)
        tween3?.finished?.await()

        boss.queueFree()
    }
}