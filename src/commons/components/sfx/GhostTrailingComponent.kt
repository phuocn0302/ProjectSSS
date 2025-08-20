package commons.components.sfx

import commons.components.Component
import commons.object_pooling.ObjectPool
import entities.PoolableEntity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.annotation.RegisterFunction
import godot.api.AnimatedSprite2D

import godot.api.PackedScene
import godot.api.Sprite2D
import godot.api.Tween
import godot.core.Color
import godot.core.toGodotName

@RegisterClass
class GhostTrailingComponent : Component() {

    @Export
    @RegisterProperty
    var ghostSpawnInterval: Double = 0.1

    @Export
    @RegisterProperty
    var ghostDecayTime: Double = 0.3

    @Export
    @RegisterProperty
    var sprite: Sprite2D? = null

    @Export
    @RegisterProperty
    var animSprite: AnimatedSprite2D? = null

    @Export
    @RegisterProperty
    var ghostTransparency: Double = 0.5

    private lateinit var objectPool: ObjectPool
    private var spawnTimer: Double = 0.0

    @RegisterFunction
    override fun _ready() {
        super._ready()
        require(sprite != null || animSprite != null) { "Sprite or AnimatedSprite must be assigned" }

        objectPool = ObjectPool()
        addChild(objectPool)

        val ghostScene = preloadGhostScene()
        objectPool.setup(ghostScene)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        spawnTimer += delta
        if (spawnTimer >= ghostSpawnInterval) {
            spawnGhost()
            spawnTimer = 0.0
        }
    }

    @RegisterFunction
    override fun activate() {
        spawnTimer = 0.0
        super.activate()
    }

    @RegisterFunction
    override fun deactivate() {
        spawnTimer = 0.0
        super.deactivate()
    }

    @RegisterFunction
    fun spawnGhost() {
        val ghost = objectPool.getInstance() ?: return
        val ghostSprite = ghost.getNode("Ghost") as? Sprite2D ?: return

        sprite?.let { spr ->
            ghostSprite.texture = spr.texture
            ghost.globalPosition = spr.globalPosition
            ghost.globalRotation = spr.globalRotation
            ghost.scale = spr.scale
        } ?: animSprite?.let { anim ->
            ghostSprite.texture = anim.spriteFrames?.getFrameTexture(anim.animation, anim.frame)
            ghost.globalPosition = anim.globalPosition
            ghost.globalRotation = anim.globalRotation
            ghost.scale = anim.scale
        }

        ghostSprite.modulate = Color(1.0, 1.0, 1.0, ghostTransparency)

        ghost.createTween()
            ?.tweenProperty(ghostSprite, "modulate:a", 0.0, ghostDecayTime)
            ?.setTrans(Tween.TransitionType.LINEAR)
            ?.setEase(Tween.EaseType.OUT)
            ?.finished
            ?.connect(this, {ghost.deactivate()})
    }

    private fun preloadGhostScene(): PackedScene {
        val ghostNode = PoolableEntity()
        val ghostSprite = Sprite2D()
        ghostSprite.setName("Ghost")
        ghostSprite.zIndex = -1
        ghostSprite.visible = true

        ghostNode.addChild(ghostSprite)
        ghostSprite.owner = ghostNode

        val ghostScene = PackedScene()
        ghostScene.pack(ghostNode)
        return ghostScene
    }
}
