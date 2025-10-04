package utils.scenes.ui

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.api.ColorRect
import godot.api.Control
import godot.api.Label
import godot.api.TextureButton
import godot.core.connect
import godot.core.signal0
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs

@RegisterClass
class StageRetry : Control() {

    @RegisterSignal
    val noPressed by signal0()

    @RegisterSignal
    val yesPressed by signal0()

    private lateinit var bg: ColorRect
    private lateinit var dialogControl: Control
    private lateinit var youDied: Label
    private lateinit var noButton: TextureButton
    private lateinit var yesButton: TextureButton

    @RegisterFunction
    override fun _ready() = godotCoroutine(context = GodotDispatchers.MainThread) {
        bg = getNodeAs("%BG")!!
        dialogControl = getNodeAs("%DialogControl")!!
        youDied = getNodeAs("%YouDied")!!
        noButton = getNodeAs("%NoButton")!!
        yesButton = getNodeAs("%YesButton")!!

        val defaultBgA = bg.modulate.a

        bg.modulate.a = 0.0
        dialogControl.modulate.a = 0.0
        youDied.modulate.a = 0.0

        val tween = createTween()?.setParallel(true)
        tween?.tweenProperty(bg, "modulate:a", defaultBgA, 1.0)
        tween?.tweenProperty(youDied, "modulate:a", 1.0, 1.0)
        tween?.chain()
        tween?.tweenProperty(dialogControl, "modulate:a", 1.0, 0.5)

        noButton.mouseEntered.connect { noButton.grabFocus() }
        yesButton.mouseEntered.connect { yesButton.grabFocus() }

        yesButton.grabFocus()
    }

    @RegisterFunction
    fun onNoButtonPressed(free: Boolean = true) = godotCoroutine(context = GodotDispatchers.MainThread) {
        noPressed.emit()
        if (free) {
            selfFree()
        }
    }

    @RegisterFunction
    fun onYesButtonPressed(free: Boolean = true) = godotCoroutine(context = GodotDispatchers.MainThread) {
        yesPressed.emit()
        if (free) {
            selfFree()
        }
    }

    private fun selfFree() = godotCoroutine(context = GodotDispatchers.MainThread) {
        val tween = createTween()
        tween?.tweenProperty(this@StageRetry, "modulate:a", 0.0, 0.1)

        tween?.finished?.await()

        queueFree()
    }
}
