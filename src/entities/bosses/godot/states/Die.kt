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

        Utils.createTimer(this@GodotBossDieState, 1.0)
            ?.timeout
            ?.await()

        val vfx = CIRCLE_EXPLOSION!!.instantiate() as Node2D
        vfx.globalPosition = boss.globalPosition
        getTree()?.currentScene?.addChild(vfx)

        boss.animatedSprite2D.hide()

        Utils.createTimer(this@GodotBossDieState, 1.0)
            ?.timeout
            ?.await()

        boss.eyeParticles.emitting = false

        Utils.createTimer(this@GodotBossDieState, 1.0)
            ?.timeout
            ?.await()

        boss.queueFree()
    }
}