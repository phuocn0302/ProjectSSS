package commons.lasers

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterProperty
import godot.api.Resource
import godot.core.Color
import godot.core.Vector2

@RegisterClass
class LaserData : Resource() {

    @Export
    @RegisterProperty
    var collisionMask: Int = 1

    @Export
    @RegisterProperty
    var direction: Vector2 = Vector2.DOWN

    @Export
    @RegisterProperty
    var length: Int = 320

    @Export
    @RegisterProperty
    var maxWidth: Int = 12

    @Export
    @RegisterProperty
    var emitTime: Double = 1.0

    @Export
    @RegisterProperty
    var castTime: Double = 0.2

    @Export
    @RegisterProperty
    var decayTime: Double = 0.2

    @Export
    @RegisterProperty
    var outlineColor: Color = Color.red

}