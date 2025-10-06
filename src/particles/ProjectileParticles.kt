package particles

import entities.projectiles.Projectile
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.CPUParticles2D
import godot.core.connect

@RegisterClass
class CPUProjectileParticles : CPUParticles2D() {

    @RegisterFunction
    override fun _ready() {
        val projectile = getParent() as Projectile
        this.localCoords = true

        projectile.activated.connect { this.emitting = true }
        projectile.deactivated.connect { this.emitting = false }
    }
}