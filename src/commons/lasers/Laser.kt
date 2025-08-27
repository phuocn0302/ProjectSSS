package commons.lasers

import commons.components.etc.HitboxComponent
import commons.singletons.Utils
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.common.util.PI
import godot.core.PackedVector2Array
import godot.core.Vector2
import godot.core.Vector3
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD.load

@RegisterClass
class Laser : Line2D() {

    companion object {
        val LASER_PARTICLE_MAT = load<ParticleProcessMaterial>("res://src/particles/materials/laser_particle_mat.tres")
    }

    @Export
    @RegisterProperty
    var laserData: LaserData = LaserData()
        set(value) {
            field = value
            setupHitbox()
            setupParticles()
        }

    @RegisterProperty
    var endPoint: Vector2 = Vector2.ZERO

    var particles: GPUParticles2D? = null
    var hitboxComponent: HitboxComponent? = null
    var hitboxCollision: CollisionShape2D? = null

    private var castTween: Tween? = null
    private var decayTween: Tween? = null
    private var emitTimer: SceneTreeTimer? = null

    private var isEmitting: Boolean = false

    @RegisterFunction
    override fun _ready() {
        setupLaser()

        laserData = laserData
    }

    @RegisterFunction
    override fun _draw() {
        if (!isEmitting) {
            return
        }

        with(laserData) {
            // Outline
            drawPolyline(points, outlineColor, (maxWidth + 2).toFloat())
            drawCircle(points[0], ((maxWidth + 2) / 2).toFloat(), outlineColor)
            drawCircle(points[1], ((maxWidth + 2) / 2).toFloat(), outlineColor)

            // Main laser
            drawPolyline(points, defaultColor, maxWidth.toFloat())
            drawCircle(points[0], (maxWidth / 2).toFloat(), defaultColor)
            drawCircle(points[1], (maxWidth / 2).toFloat(), defaultColor)
        }
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        this.points = PackedVector2Array(listOf(Vector2.ZERO, endPoint))
    }

    @RegisterFunction
    fun cast() = godotCoroutine(context = GodotDispatchers.MainThread) {
        setupLaser()

        with(laserData) {
            particles?.emitting = true

            castTween = createTween()
            castTween?.let {
                it.setParallel(true)
                it.setTrans(Tween.TransitionType.EXPO)
                it.setEase(Tween.EaseType.IN_OUT)

                it.tweenProperty(
                    this@Laser,
                    "end_point",
                    direction * length,
                    castTime / 2
                )

                it.tweenProperty(
                    this@Laser,
                    "width",
                    maxWidth,
                    castTime
                )

                it.finished.await()

                isEmitting = true

                hitboxComponent?.active = true

                emitTimer = Utils.createTimer(this@Laser, emitTime)
                emitTimer?.timeout?.connect(this@Laser, Laser::onEmitTimerTimeout)
            }
        }
    }

    @RegisterFunction
    fun stopCasting() {
        castTween?.stop()
        emitTimer?.timeout?.disconnect(this, Laser::onEmitTimerTimeout)

        isEmitting = false
        hitboxComponent?.active = false

        decay()
    }

    @RegisterFunction
    fun decay() {
        with(laserData) {
            decayTween = createTween()
            decayTween?.let {
                it.setTrans(Tween.TransitionType.SINE)
                it.setEase(Tween.EaseType.OUT)
                it.tweenProperty(
                    this@Laser,
                    "width",
                    0,
                    decayTime
                )
            }
        }
    }

    @RegisterFunction
    fun onEmitTimerTimeout() {
        isEmitting = false

        hitboxComponent?.active = false
        decay()
    }

    private fun setupLaser() {
        this.beginCapMode = Line2D.LineCapMode.ROUND
        this.endCapMode = Line2D.LineCapMode.ROUND
        this.defaultColor.a = 1.0
        this.width = 1F

        this.points = PackedVector2Array(listOf(Vector2.ZERO, Vector2.ZERO))

        endPoint = Vector2.ZERO
    }

    private fun setupParticles() {
        if (particles != null) {
            particles = GPUParticles2D()
            this.addChild(particles)
        }

        with(laserData) {
            particles?.let {
                it.amount = length / 2
                it.position = direction * (length / 2)
                it.lifetime = emitTime
                it.randomness = 100F
                it.showBehindParent = true
                it.emitting = false
                it.oneShot = true
                it.rotation = (direction.angle() - PI / 2).toFloat()

                LASER_PARTICLE_MAT?.let { mat ->
                    val particlesMat = mat.duplicate() as ParticleProcessMaterial
                    particlesMat.emissionBoxExtents = Vector3(maxWidth / 2 + 3, length / 2, 1)

                    it.processMaterial = particlesMat
                }
            }
        }
    }

    private fun setupHitbox() {
        if (hitboxComponent != null) {
            hitboxComponent = HitboxComponent()
            this.addChild(hitboxCollision)
        }

        if (hitboxCollision != null) {
            hitboxCollision = CollisionShape2D()
            hitboxCollision!!.shape = RectangleShape2D()

            hitboxComponent!!.addChild(hitboxCollision)
        }

        with(laserData) {
            hitboxCollision?.let {
                (it.shape as RectangleShape2D).size = Vector2(maxWidth - 1, length - 1)
                it.position = Vector2(0, length / 2)
            }

            hitboxComponent?.let {
                it.rotation = (direction.angle() - PI / 2).toFloat()
                it.active = false
                it.collisionMask = collisionMask.toLong()
            }
        }
    }
}