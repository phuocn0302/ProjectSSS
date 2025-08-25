package commons.components.etc

import commons.components.Component
import commons.singletons.Utils
import godot.annotation.*
import godot.api.SceneTreeTimer
import godot.core.connect
import godot.core.signal0
import godot.global.GD

@RegisterClass
class IFrameComponent : Component() {

	@RegisterSignal
	val onIframeActivated by signal0()

	@Export
	@RegisterProperty
	var hurtboxComponent: HurtboxComponent? = null

    @Export
	@RegisterProperty
	var iframeDuration: Double = 0.3

	private var currentTimer: SceneTreeTimer? = null

	@RegisterFunction
	override fun _ready() {
		GD.assert((hurtboxComponent != null), "HurtboxComponent must not be null")

		hurtboxComponent!!.hurt.connect { startIFrame() }
	}

	@RegisterFunction
	fun startIFrame(duration: Double = -1.0) {
		hurtboxComponent?.callDeferred("deactivate")
		onIframeActivated.emit()

		val time = if (duration < 0) iframeDuration else duration
		currentTimer = Utils.createTimer(this, time)
		currentTimer?.timeout?.connect(this, IFrameComponent::onIFrameTimeout)
	}

	@RegisterFunction
	fun onIFrameTimeout() {
		hurtboxComponent?.callDeferred("activate")
	}
}
