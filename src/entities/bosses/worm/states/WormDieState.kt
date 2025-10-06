package entities.bosses.worm.states

import commons.singletons.Utils
import entities.bosses.worm.WormBoss
import entities.enemies.common_states.EnemyDieState
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.Tween
import godot.core.Vector2
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD
import godot.global.GD.load

@RegisterClass
class WormDieState : EnemyDieState() {

    companion object {
        val CIRCLE_EXPLOSION = load<PackedScene>("res://src/particles/circle_explosion.tscn")
    }

    lateinit var boss: WormBoss

    @RegisterFunction
    override fun enter() = godotCoroutine(context = godot.coroutines.GodotDispatchers.MainThread) {
        boss = getNodeAs("../..")!!

        super.enter()

        val wait = Utils.createTweenTimer(this@WormDieState, 2.0)

        wait?.finished?.await()

        val tween = createTween()?.setTrans(Tween.TransitionType.QUAD)?.setEase(Tween.EaseType.OUT)
        tween?.setParallel(true)
        tween?.tweenProperty(boss, "global_rotation", 0, 0.5)
        tween?.tweenProperty(boss, "global_position", boss.defaultPosition, 1.0)
        tween?.chain()?.setParallel(false)
        tween?.tweenProperty(boss, "global_position", boss.defaultPosition + Vector2.UP * 30.0, 1.0)
        tween?.tweenProperty(boss, "global_position", boss.defaultPosition + Vector2.DOWN * 120.0, 0.3)

        for (i in boss.segments.size - 1 downTo 0) {
            boss.segments[i].explode()
            val d = Utils.createTweenTimer(this@WormDieState, 0.1)
            d?.finished?.await()
        }

        val endWait = Utils.createTweenTimer(this@WormDieState, 0.5)
        endWait?.finished?.await()

        val vfx = CIRCLE_EXPLOSION!!.instantiate()
        if (vfx is Node2D) vfx.globalPosition = boss.globalPosition
        getTree()?.currentScene?.addChild(vfx)

        boss.queueFree()
    }
}




