package commons.components.movements

import commons.components.Component
import commons.singletons.Utils
import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.VariantArray
import godot.core.Vector2
import godot.core.variantArrayOf
import godot.global.GD


@RegisterClass
class MovementBoundsComponent : Component() {

    @RegisterProperty
    var bounds: VariantArray<Vector2> = variantArrayOf(
        Utils.SCREEN_SIZE.position,
        Utils.SCREEN_SIZE.size
    )

    @RegisterFunction
    override fun _ready() {
        GD.assert(entity != null)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        val pos = entity!!.globalPosition

        val clampedX = GD.clamp(pos.x, bounds[0].x, bounds[1].x)
        val clampedY = GD.clamp(pos.y, bounds[0].y, bounds[1].y)

        entity!!.globalPosition = Vector2(clampedX, clampedY)
    }
}