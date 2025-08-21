package entities

import commons.object_pooling.ObjectPool
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.core.signal0

@RegisterClass
open class PoolableEntity: Entity() {

    @RegisterSignal("activated")
    val activated by signal0()

    @RegisterSignal("deactivated")
    val deactivated by signal0()

    @Export
    @RegisterProperty
    var pool: ObjectPool? = null

    @RegisterFunction
    open fun deactivate() {
        this.setVisible(false)
        this.setProcess(false)
        this.setPhysicsProcess(false)

        pool?.returnInstance(this)

        deactivated.emit()
    }

    @RegisterFunction
    open fun activate() {
        this.setVisible(true)
        this.setProcess(true)
        this.setPhysicsProcess(true)

        activated.emit()
    }

    @RegisterFunction
    fun reset() {}
}
