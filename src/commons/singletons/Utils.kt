package commons.singletons

import godot.api.Node
import godot.api.Tween
import godot.core.Rect2
import godot.core.Vector2

object Utils {
    val SCREEN_SIZE = Rect2(Vector2.ZERO, Vector2(180.0, 320.0))

    fun createTweenTimer(
        node: Node,
        duration: Double
    ): Tween? {
        val tween = node.createTween()
        tween?.bindNode(node)
        tween?.tweenInterval(duration)
        return tween
    }
}
