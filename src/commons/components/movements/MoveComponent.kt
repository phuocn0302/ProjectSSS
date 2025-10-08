package commons.components.movements

import commons.components.Component
import commons.singletons.Utils
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector2
import godot.global.GD
import kotlin.math.sin

@RegisterClass
class MoveComponent : Component() {

    enum class MoveType {
        STRAIGHT,
        SINEWAVE,
        STRAFE
    }

    @Export @RegisterProperty
    var moveType: MoveType = MoveType.STRAIGHT

    @Export @RegisterProperty
    var direction: Vector2 = Vector2.DOWN

    @Export @RegisterProperty
    var speed: Double = 70.0

    @Export @RegisterProperty
    var sineAmp: Double = 5.0

    @Export @RegisterProperty
    var sineFreq: Double = 5.0

    @Export @RegisterProperty
    var strafeDirection: Vector2 = Vector2(1.0, 1.0) // 1 = right, -1 = left

    private var sineT: Double = 0.0
    private var startingPos: Vector2 = Vector2.ZERO

    @RegisterFunction
    override fun _ready() {
        GD.assert(entity != null)

        startingPos = entity!!.globalPosition
        direction = direction.normalized()
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        when (moveType) {
            MoveType.STRAIGHT -> straightMove(delta)
            MoveType.SINEWAVE -> sinewaveMove(delta)
            MoveType.STRAFE -> strafeMove(delta)
        }
    }

    private fun straightMove(delta: Double) {
        entity!!.globalPosition += direction * speed * delta
    }

    private fun sinewaveMove(delta: Double) {
        sineT += delta
        startingPos += direction * speed * delta
        val perp = Vector2(-direction.y, direction.x)
        val sineOffset = perp * (sin(sineT * sineFreq) * sineAmp)
        entity!!.globalPosition = startingPos + sineOffset
    }

    private fun strafeMove(delta: Double) {
        // Move in strafe direction (diagonal down-left or down-right)
        val moveVector = strafeDirection * speed * delta
        entity!!.globalPosition += moveVector
        
        // Check boundaries and bounce
        checkBoundaries()
    }

    private fun checkBoundaries() {
        val pos = entity!!.globalPosition
        
        // Check if hit left or right boundary
        if (pos.x <= 0 || pos.x >= Utils.SCREEN_SIZE.size.x) {
            strafeDirection.x *= -1
        }
        
        // Check if hit top or bottom boundary
        if (pos.y <= 0 || pos.y >= Utils.SCREEN_SIZE.size.y) {
            strafeDirection.y *= -1
        }
        
        // Clamp position to screen bounds
        val clampedX = GD.clamp(pos.x, 0.0, Utils.SCREEN_SIZE.size.x)
        val clampedY = GD.clamp(pos.y, 0.0, Utils.SCREEN_SIZE.size.y)
        entity!!.globalPosition = Vector2(clampedX, clampedY)
    }
}
