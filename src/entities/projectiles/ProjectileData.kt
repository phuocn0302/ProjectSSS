package entities.projectiles

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Color
import godot.core.Vector2
import godot.global.GD.load

@RegisterClass
class ProjectileData : Resource() {

    companion object {
        val PROJECTILE_HIT_PARTICLES = load<PackedScene>("res://src/particles/projectile_hit_particles.tscn")
        val PROJECTILE_PARTICLES = load<PackedScene>("res://src/particles/projectile_particles.tscn")
    }

    @Export
    @RegisterProperty
    var texture: Texture2D? = null

    @Export
    @RegisterProperty
    var spriteFrames: SpriteFrames? = null

    @Export
    @RegisterProperty
    var textureRotateWithDir: Boolean = false

    @Export
    @RegisterProperty
    var collisionShape: Shape2D? = null

    @Export
    @RegisterProperty
    var shapeOffset: Vector2 = Vector2.ZERO

    @Export
    @RegisterProperty
    var collisionMask: Int = 1

    @Export
    @RegisterProperty
    var speed: Double = 200.0

    @Export
    @RegisterProperty
    var damage: Double = 1.0

    @Export
    @RegisterProperty
    var lifeTime: Double = 5.0

    @Export
    @RegisterProperty
    var vfxColor: Color = Color.red

    @Export
    @RegisterProperty
    var onHitVfx: PackedScene? = PROJECTILE_HIT_PARTICLES

    @Export
    @RegisterProperty
    var useParticles: Boolean = true

    @Export
    @RegisterProperty
    var particles: PackedScene? = PROJECTILE_PARTICLES
}
