package commons.components.etc

import commons.components.Component
import commons.singletons.Utils
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.SceneTreeTimer
import godot.core.signal0
import godot.global.GD

@RegisterClass
class IFrameComponent : Component() {

    @RegisterSignal
    val iframeActivated by signal0()

    @RegisterProperty
    var hurtboxComponent: HurtboxComponent? = null

    @RegisterProperty
    var iframeDuration: Double = 0.3

    private var currentTimer: SceneTreeTimer? = null

    @RegisterFunction
    override fun _ready() {
        super._ready()

        GD.assert((hurtboxComponent != null), "HurtboxComponent must not be null")

        hurtboxComponent!!.hurt.connect(this , IFrameComponent::startIFrame)
    }

    @RegisterFunction
    fun startIFrame(duration: Double = -1.0) {
        hurtboxComponent?.callDeferred("deactivate")
        iframeActivated.emit()

        val time = if (duration < 0) iframeDuration else duration
        currentTimer = Utils.createTimer(this, time)
        currentTimer?.timeout?.connect(this, IFrameComponent::onIFrameTimeout)
    }

    @RegisterFunction
    fun onIFrameTimeout() {
        hurtboxComponent?.callDeferred("activate")
    }
}
