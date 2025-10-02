package entities.projectiles

import commons.components.etc.HurtboxComponent
import commons.singletons.Utils
import entities.PoolableEntity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.AnimatedSprite2D
import godot.api.CPUParticles2D
import godot.api.CanvasItem
import godot.api.Node
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.PhysicsShapeQueryParameters2D
import godot.api.Tween
import godot.api.Shader
import godot.api.ShaderMaterial
import godot.api.Sprite2D
import godot.core.Color
import godot.core.Dictionary
import godot.core.Transform2D
import godot.core.VariantArray
import godot.core.Vector2
import godot.global.GD
import particles.CPUProjectileParticles


@RegisterClass
open class Projectile : PoolableEntity() {

    companion object {
        private val ENTITY_SHADER: Shader = GD.load("res://src/shaders/entity_shader.gdshader")!!
    }

    @Export
    @RegisterProperty
    var projectileData: ProjectileData? = null
        set(value) {
            field = value
            if (value != null) {
                setSprite()
                if (value.useParticles) {
                    setParticles()
                }
                setHitbox()
            }
        }

    var direction: Vector2 = Vector2.UP
    private var vfxSpawned: Boolean = false
    private var isVisibleProjectile: Boolean = true

    private var collisionQuery = PhysicsShapeQueryParameters2D()
    private var spriteNode: Node? = null
    private var particlesNode: CPUParticles2D? = null
    private var lifetimeTween: Tween? = null

    @RegisterFunction
    override fun _ready() {
        addToGroup("projectile")
        GD.assert(projectileData != null)
        startLifetimeTimer()
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        move(delta)
        checkVisible()
        updateHitbox()
        updateSpriteRotation()
    }

    @RegisterFunction
    override fun activate() {
        super.activate()
        resetTimer()
        startLifetimeTimer()
        collisionQuery.collideWithAreas = true
        vfxSpawned = false
    }

    @RegisterFunction
    override fun deactivate() {
        super.deactivate()
        resetTimer()
        collisionQuery.collideWithAreas = false

        if (!isVisibleProjectile) return

        val data = projectileData ?: return
        if (data.onHitVfx != null && !vfxSpawned) {
            spawnSfx(data.onHitVfx!!)
        }
    }

    open fun move(delta: Double) {
        val data = projectileData ?: return
        globalRotation = direction.angle().toFloat()
        globalPosition += direction.normalized() * data.speed * delta
    }

    private fun setSprite() {
        val data = projectileData ?: return
        val useAnimated = data.spriteFrames != null

        if (spriteNode == null || !GD.isInstanceValid(spriteNode!!)) {
            spriteNode = getNodeOrNull("ProjectileSprite2D")
        }

        if (spriteNode != null && GD.isInstanceValid(spriteNode!!)) {
            val wrongType =
                (useAnimated && spriteNode !is AnimatedSprite2D) || (!useAnimated && spriteNode !is Sprite2D)
            if (wrongType) {
                removeChild(spriteNode!!)
                spriteNode!!.queueFree()
                spriteNode = null
            }
        }

        if (spriteNode == null) {
            spriteNode = if (useAnimated) AnimatedSprite2D() else Sprite2D()
            spriteNode!!.setName("ProjectileSprite2D")
            addChild(spriteNode!!)
        }

        if (useAnimated) {
            val anim = spriteNode as AnimatedSprite2D
            anim.spriteFrames = data.spriteFrames
            anim.play("default")
        } else {
            val spr = spriteNode as Sprite2D
            spr.texture = data.texture
        }

        setShader(spriteNode as CanvasItem)
    }

    private fun setParticles() {
        val data = projectileData ?: return
        if (data.particles == null) return

        if (particlesNode == null || !GD.isInstanceValid(particlesNode!!)) {
            particlesNode = getNodeOrNull("Particles") as? CPUParticles2D
            if (particlesNode == null) {
                particlesNode = data.particles!!.instantiate() as CPUProjectileParticles
                particlesNode!!.setName("Particles")
                addChild(particlesNode!!)
            }
        }

        particlesNode?.modulate = data.vfxColor
    }

    private fun setHitbox() {
        val data = projectileData ?: return
        collisionQuery.shape = data.collisionShape
        collisionQuery.collideWithBodies = false
        collisionQuery.collideWithAreas = true
        collisionQuery.collisionMask = data.collisionMask.toLong()

        val offsetTransform = Transform2D.IDENTITY.also {
            it.origin = data.shapeOffset
        }
        collisionQuery.transform = globalTransform * offsetTransform
    }

    private fun setShader(sprite: CanvasItem) {
        val mat = ShaderMaterial().apply {
            shader = ENTITY_SHADER
            setShaderParameter("outline_enabled", true)
            setShaderParameter("outline_color", projectileData?.vfxColor ?: Color.white)
        }
        sprite.material = mat
    }

    private fun updateHitbox() {
        val data = projectileData ?: return
        val offsetTransform = Transform2D.IDENTITY.also {
            it.origin = data.shapeOffset
        }
        collisionQuery.transform = globalTransform * offsetTransform

        val hits = getWorld2d()?.directSpaceState?.intersectShape(collisionQuery)
        if (hits!!.isNotEmpty()) {
            onCollision(hits)
        }
    }

    private fun updateSpriteRotation() {
        val data = projectileData ?: return
        if (!data.textureRotateWithDir && spriteNode is CanvasItem) {
            (spriteNode as Node2D).globalRotation = Vector2.ZERO.angle().toFloat()
        }
    }

    private fun checkVisible() {
        isVisibleProjectile = Utils.SCREEN_SIZE.grow(10.0).hasPoint(globalPosition)
        visible = isVisibleProjectile

        val expanded = Utils.SCREEN_SIZE.grow(50.0)
        if (!expanded.hasPoint(globalPosition)) {
            deactivate()
        }
    }

    private fun onCollision(hits: VariantArray<Dictionary<Any?, Any?>>) {
        for (hit in hits) {
            val collider = hit["collider"]
            if (collider is HurtboxComponent) {
                if (!collider.active) continue
                collider.takeDamage(projectileData!!.damage)
                deactivate()
            }
        }
    }

    private fun startLifetimeTimer() {
        val data = projectileData ?: return
        lifetimeTween = Utils.createTweenTimer(this, data.lifeTime)
        lifetimeTween?.finished?.connect(this, Projectile::deactivate)
    }

    private fun resetTimer() {
        lifetimeTween?.kill()
        lifetimeTween = null
    }

    private fun spawnSfx(vfx: PackedScene) {
        val fx = vfx.instantiate()
        if (fx is Node2D) {
            fx.globalPosition = globalPosition
        }
        getTree()?.currentScene!!.addChild(fx)
        vfxSpawned = true
    }
}
