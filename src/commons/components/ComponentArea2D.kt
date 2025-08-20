package commons.components


import entities.Entity
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Area2D
import godot.core.connect

@RegisterClass
abstract class ComponentArea2D : Area2D() {

    private var _active: Boolean = true

    @Export
    @RegisterProperty
    var active: Boolean
        get() = _active
        set(value) {
            _active = value
            setActiveState(value)
        }

    @RegisterFunction
    override fun _ready() {
        areaEntered.connect { area -> onAreaEntered(area) }
        areaExited.connect { area -> onAreaExited(area) }
    }

    @RegisterFunction
    open fun onAreaEntered(area: Area2D) {
        // Override in subclasses or connect signals
    }

    @RegisterFunction
    open fun onAreaExited(area: Area2D) {
        // Override in subclasses or connect signals
    }

    private fun setActiveState(value: Boolean) {
        monitorable = value
        monitoring = value
    }

    @RegisterFunction
    open fun activate() {
        active = true
    }

    @RegisterFunction
    open fun deactivate() {
        active = false
    }
}
