package commons.components

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.api.Node
import godot.core.VariantArray
import godot.global.GD

@RegisterClass
open class ComponentContainer : Node2D() {

    @RegisterFunction
    fun getComponentList(): VariantArray<Node> {
        val arr = VariantArray<Node>()
        for (child in getChildren()) {
            if (child is Component || child is Component2D || child is ComponentArea2D) {
                arr.add(child)
            }
        }
        return arr
    }

    @RegisterFunction
    fun disableAllComponents() {
        for (c in getComponentList()) {
            when (c) {
                is Component -> c.deactivate()
                is Component2D -> c.deactivate()
                is ComponentArea2D -> c.deactivate()
            }
        }
    }
}
