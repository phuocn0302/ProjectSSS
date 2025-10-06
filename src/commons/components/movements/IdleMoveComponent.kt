package commons.components.movements

import commons.components.Component
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.Vector2
import godot.global.GD
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@RegisterClass
class IdleMoveComponent : Component() {

    enum class MoveType {
        CIRCLE,
        FIGURE8,
        OSCILLATE
    }

    @Export @RegisterProperty
    var moveType: MoveType = MoveType.CIRCLE

    @Export @RegisterProperty
    var direction: Vector2 = Vector2.LEFT

    @Export @RegisterProperty
    var angularSpeed: Double = 1.0

    @Export @RegisterProperty
    var circleRadius: Double = 1.0

    @Export @RegisterProperty
    var figure8Amplitude: Double = 1.0

    @Export @RegisterProperty
    var oscSpeed: Double = 1.0

    @Export @RegisterProperty
    var oscRange: Double = 50.0

    private var t: Double = 0.0
    private var prevOffset: Vector2 = Vector2.ZERO

    @RegisterFunction
    override fun _ready() {
        GD.assert(entity != null)

        direction = direction.normalized()
        active = active
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        t = GD.fposmod(t + angularSpeed * delta, 2 * PI)

        when (moveType) {
            MoveType.CIRCLE -> circleMove()
            MoveType.FIGURE8 -> figure8Move()
            MoveType.OSCILLATE -> oscillateMove()
        }
    }

    private fun circleMove() {
        val localOffset = Vector2(
            circleRadius * cos(t),
            circleRadius * sin(t)
        )
        val rotatedOffset = localOffset.rotated(direction.angle())
        val deltaOffset = rotatedOffset - prevOffset
        entity!!.globalPosition += deltaOffset
        prevOffset = rotatedOffset
    }

    private fun figure8Move() {
        val a = figure8Amplitude
        val localOffset = Vector2(
            a * cos(t),
            a * sin(t) * cos(t)
        )
        val rotatedOffset = localOffset.rotated(direction.angle())
        val deltaOffset = rotatedOffset - prevOffset
        entity!!.globalPosition += deltaOffset
        prevOffset = rotatedOffset
    }

    private fun oscillateMove() {
        val localOffset = direction * (oscRange * sin(t * oscSpeed))
        val deltaOffset = localOffset - prevOffset
        entity!!.globalPosition +=  deltaOffset
        prevOffset = localOffset
    }
}