package commons.components

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node2D
import godot.global.GD

@RegisterClass
abstract class Component2D : Node2D() {

    private var _active: Boolean = true

    @Export
    @RegisterProperty
    var active: Boolean
        get() = _active
        set(value) {
            _active = value
            if (_active) activate() else deactivate()
        }

    @RegisterFunction
    open fun activate() {
        setProcess(true)
        setPhysicsProcess(true)
    }

    @RegisterFunction
    open fun deactivate() {
        setProcess(false)
        setPhysicsProcess(false)
    }
}
