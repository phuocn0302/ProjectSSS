package commons.object_pooling

import entities.PoolableEntity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node
import godot.api.PackedScene
import godot.core.VariantArray
import godot.core.variantArrayOf
import godot.global.GD

@RegisterClass
class ObjectPool : Node() {

    @Export
    @RegisterProperty
    var objectToPool: PackedScene? = null

    @Export
    @RegisterProperty
    var nodeToPool: Node? = null

    private var isInitialized: Boolean = false
    private val availablePool = variantArrayOf<Any>()

    @RegisterFunction
    fun setup(scene: PackedScene) {
        if (isInitialized) return
        objectToPool = scene
        isInitialized = true
    }

    @RegisterFunction
    fun setupNode(node: Node) {
        if (isInitialized) return
        nodeToPool = node

        isInitialized = true
    }

    @RegisterFunction
    fun getInstance(): PoolableEntity {
        val obj = if (availablePool.isEmpty()) {
            addToPool()
        } else {
            availablePool.popBack()
        }

        (obj as PoolableEntity).activate()
        return obj
    }

    @RegisterFunction
    fun returnInstance(obj: PoolableEntity) {
        availablePool.pushBack(obj)
    }

    private fun addToPool(): PoolableEntity {
        val obj: PoolableEntity = when {
            objectToPool != null -> objectToPool!!.instantiate() as PoolableEntity
            nodeToPool != null -> nodeToPool!!.duplicate() as PoolableEntity
            else -> error("ObjectPool not initialized with a valid object or node")
        }

        obj.pool = this

        addChild(obj, true)
        return obj
    }
}
