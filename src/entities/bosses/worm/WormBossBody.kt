package entities.bosses.worm

import commons.components.ComponentContainer
import commons.components.etc.HitboxComponent
import commons.components.etc.HurtboxComponent
import commons.components.sfx.HitFlashComponent
import entities.Entity
import entities.projectiles.ProjectileSpawner
import entities.projectiles.spawners.ProjectileSpawnerContainer
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.AnimatedSprite2D
import godot.api.Marker2D
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.VisibleOnScreenNotifier2D
import godot.core.Vector2
import godot.extension.getNodeAs
import godot.global.GD.load

@RegisterClass
class WormBossBody : Entity() {

    companion object {
        val CIRCLE_EXPLOSION = load<PackedScene>("res://src/particles/circle_explosion.tscn")
    }

    @RegisterProperty
    @Export
    var wormBoss: WormBoss? = null

    @RegisterProperty
    @Export
    var moveSpeed: Double = 15.0

    @RegisterProperty
    @Export
    var nextSegment: Entity? = null

    @RegisterProperty
    @Export
    var nextSegmentJoint: Marker2D? = null

    lateinit var jointSegmentPos: Marker2D
    lateinit var sprite: AnimatedSprite2D
    lateinit var hitFlashComponent: HitFlashComponent
    lateinit var hurtboxComponent: HurtboxComponent
    lateinit var hitboxComponent: HitboxComponent
//    lateinit var circleProjectileSpawner: ProjectileSpawner
//    lateinit var targetProjectileSpawner: ProjectileSpawner
    lateinit var shootTrigger: VisibleOnScreenNotifier2D
    lateinit var components: ComponentContainer
    lateinit var spawners: ProjectileSpawnerContainer

    @RegisterFunction
    override fun _ready() {
        jointSegmentPos = getNodeAs("%JointSegmentPos")!!
        sprite = getNodeAs("%AnimatedSprite2D")!!
        hitFlashComponent = getNodeAs("%HitFlashComponent")!!
        hurtboxComponent = getNodeAs("%HurtboxComponent")!!
        hitboxComponent = getNodeAs("%HitboxComponent")!!
//        circleProjectileSpawner = getNodeAs("%CircleProjectileSpawner")!!
//        targetProjectileSpawner = getNodeAs("%TargetProjectileSpawner")!!
        shootTrigger = getNodeAs("%ShootTrigger")!!
        components = getNodeAs("%Components")!!
        spawners = getNodeAs("%Spawners")!!

        require(wormBoss != null)

        hurtboxComponent.healthComponent = wormBoss!!.healthComponent
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        val joint = nextSegmentJoint ?: return
        globalPosition = globalPosition.lerp(joint.globalPosition, moveSpeed * delta)

        val nextSeg = nextSegment ?: return
        val targetAngle = globalPosition.directionTo(nextSeg.globalPosition).angle() - Math.PI / 2
        rotation += ((targetAngle - rotation) * (delta * 100.0)).toFloat()
    }

    @RegisterFunction
    fun explode() {
        val vfx = CIRCLE_EXPLOSION!!.instantiate()
        if (vfx is Node2D) {
            vfx.globalPosition = globalPosition
        }
        getTree()?.currentScene?.addChild(vfx)
        queueFree()
    }
}



