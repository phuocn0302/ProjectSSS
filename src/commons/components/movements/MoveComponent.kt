package commons.components.movements

import commons.components.Component
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
        SINEWAVE
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
}
