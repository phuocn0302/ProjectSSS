package utils.scenes.ui

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Control
import godot.api.InputEvent
import godot.api.TextureButton
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs

@RegisterClass
class QuitGameConfirm : Control() {

	private lateinit var noButton: TextureButton
	private lateinit var yesButton: TextureButton

	@RegisterFunction
	override fun _ready() {
		noButton = getNodeAs("%NoButton")!!
		yesButton = getNodeAs("%YesButton")!!

		noButton.grabFocus()

		noButton.focusNeighborRight = yesButton.getPath()
		yesButton.focusNeighborLeft = noButton.getPath()
	}

	@RegisterFunction
	override fun _input(event: InputEvent?) {
		event?.let {
			if (it.isActionPressed("ui_cancel")) {
				onNoButtonPressed()
			}
		}
	}

	@RegisterFunction
	fun onNoButtonPressed() = godotCoroutine(context = GodotDispatchers.MainThread) {
		val tween = createTween()
		tween?.tweenProperty(this@QuitGameConfirm, "modulate:a", 0.0, 0.1)

		tween?.finished?.await()
		queueFree()
	}

	@RegisterFunction
	fun onYesButtonPressed() {
		getTree()?.quit()
	}
}
