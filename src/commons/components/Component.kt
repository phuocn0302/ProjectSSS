package commons.components

import entities.Entity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node
import godot.global.GD

@RegisterClass
abstract class Component : Node() {

    @Export
    @RegisterProperty
    var entity: Entity? = null

    private var _active: Boolean = true

    @Export
    @RegisterProperty
    var active: Boolean
        get() = _active
        set(value) {
            if (value) activate() else deactivate()
        }

    @RegisterFunction
    open fun activate() {
        _active = true
        setProcess(true)
        setPhysicsProcess(true)
    }

    @RegisterFunction
    open fun deactivate() {
        _active = false
        setProcess(false)
        setPhysicsProcess(false)
    }
}
