package particles

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.GPUParticles2D
import godot.core.connect

@RegisterClass
class GPUOneshotParticles : GPUParticles2D() {

    @RegisterFunction
    override fun _ready() {
        this.emitting = false
        this.oneShot = true
        this.emitting = true
        this.finished.connect { this.queueFree() }
    }
}