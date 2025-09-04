package utils.scenes.ui

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Control
import godot.api.PackedScene
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class SplashScreen : Control() {

    @Export
    @RegisterProperty
    var sceneToLoad: PackedScene? = null

    @Export
    @RegisterProperty
    var duration: Double = 1.0

    @RegisterFunction
    override fun _ready() = godotCoroutine(context = GodotDispatchers.MainThread) {
        modulate.a = 0.0

        val tween = getTree()?.createTween()
        tween?.tweenProperty(this@SplashScreen, "modulate:a", 1.0, 0.5)

        tween?.tweenInterval(duration)

        tween?.tweenProperty(this@SplashScreen, "modulate:a", 0.0, 0.5)

        tween?.finished?.await()

        sceneToLoad?.let {
            getTree()?.changeSceneToPacked(it)
        }

    }

}