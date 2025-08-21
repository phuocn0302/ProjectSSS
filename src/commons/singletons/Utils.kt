package commons.singletons

import godot.api.Node
import godot.api.SceneTreeTimer
import godot.core.Rect2
import godot.core.Vector2

object Utils {
    val SCREEN_SIZE = Rect2(Vector2.ZERO, Vector2(180.0, 320.0))

    fun createTimer(
        node: Node,
        duration: Double,
        processAlways: Boolean = false,
        processInPhysics: Boolean = false,
        ignoreTimeScale: Boolean = false
    ): SceneTreeTimer? {
        return node.getTree()?.createTimer(duration, processAlways, processInPhysics, ignoreTimeScale)
    }
}
