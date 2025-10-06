package commons.components.sfx

import commons.components.Component
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Camera2D
import godot.api.Tween
import godot.core.Vector2
import godot.core.connect
import kotlin.random.Random

@RegisterClass
class ScreenShakeComponent : Component() {

	@RegisterProperty
	var shakeAmount: Double = 3.0

	@RegisterProperty
	var shakeDuration: Double = 0.2
	
	@RegisterProperty
	var shakeT: Double = 0.0

	private var camera: Camera2D? = null

	private var tween: Tween? = null
	private var isShaking: Boolean = false

	@RegisterFunction
	override fun _process(delta: Double) {
		if (camera == null) {
			camera = getViewport()?.getCamera2d()
		}

		val cam = camera ?: return
		if (!isShaking) return

		cam.offset = if (shakeT > 0) randomOffset() else Vector2.ZERO
	}

	@RegisterFunction
	override fun _exitTree() {
		camera?.offset = Vector2.ZERO
	}

	@RegisterFunction
	fun shake() {
		isShaking = true
		shakeT = shakeAmount

		tween = createTween()
		tween?.tweenProperty(this, "shake_t", 0.0, shakeDuration)
		tween?.finished?.connect { isShaking = false }
	}

	private fun randomOffset(): Vector2 {
		val x = Random.nextDouble(-shakeT, shakeT)
		val y = Random.nextDouble(-shakeT, shakeT)
		return Vector2(x, y)
	}
}
