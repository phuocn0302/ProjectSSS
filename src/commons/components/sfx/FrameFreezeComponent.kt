package commons.components.sfx


import commons.components.Component
import commons.singletons.Utils
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Engine
import godot.api.Node
import godot.coroutines.await
import godot.coroutines.godotCoroutine


@RegisterClass
class FrameFreezeComponent : Component() {

    @Export
    @RegisterProperty
    var freezeDuration: Double = 0.3

    @Export
    @RegisterProperty
    var freezeAmount: Double = 0.3

    @RegisterFunction
    override fun _exitTree() {
        if (Engine.timeScale < 1.0) {
            Engine.timeScale = 1.0
        }
    }

    @RegisterFunction
    fun freeze() = godotCoroutine {
        Engine.timeScale = freezeAmount

        val timer = Utils.createTimer(this@FrameFreezeComponent, freezeDuration)
        timer?.timeout?.await()

        Engine.timeScale = 1.0
    }

}
