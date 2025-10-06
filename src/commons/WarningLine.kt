package commons

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.CanvasItem
import godot.api.Line2D
import godot.api.Texture2D
import godot.core.PackedVector2Array
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class MoveWarningLine : Line2D() {

    @Export
    @RegisterProperty
    var lineTexture: Texture2D? = GD.load("res://assets/texture/sfx/line/med_move_warning_line.png")

    @Export
    @RegisterProperty
    var showTime: Double = 0.5

    @Export
    @RegisterProperty
    var direction: Vector2 = Vector2.DOWN

    @Export
    @RegisterProperty
    var length: Double = 320.0

    @RegisterProperty
    var endPoint: Vector2 = Vector2.ZERO

    @RegisterFunction
    override fun _ready() {
        addToGroup("warning_line")

        texture = lineTexture
        textureMode = Line2D.LineTextureMode.TILE
        width = (lineTexture?.getHeight() ?: 1).toFloat()
        textureRepeat = CanvasItem.TextureRepeat.ENABLED
        zIndex = -999

        points = PackedVector2Array(listOf(Vector2.ZERO, Vector2.ZERO))
        visibilityChanged.connect(this, MoveWarningLine::onVisibilityChange)

        hide()
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        if (!visible) return
        points = PackedVector2Array(listOf(Vector2.ZERO, endPoint))
    }

    @RegisterFunction
    fun onVisibilityChange() {
        if (!visible) {
            endPoint = Vector2.ZERO
            points = PackedVector2Array(listOf(Vector2.ZERO, Vector2.ZERO))
            return
        }

        val tween = createTween()
        tween!!.tweenProperty(this, "end_point", direction.normalized() * length, showTime)
    }
}
