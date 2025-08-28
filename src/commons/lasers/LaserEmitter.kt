package commons.lasers

import commons.components.Component2D
import commons.components.sfx.ScreenShakeComponent
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Timer
import godot.core.Callable
import godot.core.toGodotName
import godot.global.GD

@RegisterClass
class LaserEmitter : Component2D() {

    @Export
    @RegisterProperty
    var laserData: LaserData = LaserData()
        set(value) {
            field = value
            setupLaser()
        }

    @Export
    @RegisterProperty
    var screenShake: Boolean = true

    @Export
    @RegisterProperty
    var auto: Boolean = false
        set(value) {
            field = value

            if (value) {
                activateEmitTimer()
            } else {
                deactivateEmitTimer()
            }
        }

    @Export
    @RegisterProperty
    var emitInterval: Double = 3.0

    private var timer: Timer? = null
    private var laser: Laser? = null
    private var screenShakeComponent: ScreenShakeComponent? = null

    @RegisterFunction
    override fun _ready() {
        if (screenShake && screenShakeComponent == null) {
            screenShakeComponent = ScreenShakeComponent()
            screenShakeComponent?.shakeDuration = 0.5

            this.addChild(screenShakeComponent)
        }

        // Force setter func to run
        auto = auto
        laserData = laserData
        emitInterval = emitInterval
    }

    @RegisterFunction
    fun emitLaser() {
        laser?.let {
            screenShakeComponent?.let { screenShakeComponent ->
                if (screenShake) screenShakeComponent.shake()
            }

            it.cast()
        }
    }

    @RegisterFunction
    fun forceStopEmit() {
        laser?.stopCasting()
    }

    private fun setupLaser() {
        if (laser == null) {
            laser = Laser()
            this.addChild(laser)
        }

        laser?.laserData = laserData
    }

    private fun activateEmitTimer() {
        if (timer == null) {
            timer = Timer()
            this.addChild(timer)
        }

        timer?.let {
            if (it.isStopped() && it.isInsideTree()) {
                it.start(emitInterval)
            }

            it.waitTime = emitInterval
            it.autostart = true

            if (!it.timeout.isConnected(Callable(this, "emitLaser".toGodotName()))) {
                it.timeout.connect(this, LaserEmitter::emitLaser)
            }
        }
    }

    private fun deactivateEmitTimer() {
        timer?.let {
            it.timeout.disconnect(this, LaserEmitter::emitLaser)
            it.stop()
            it.queueFree()
        }
    }
}