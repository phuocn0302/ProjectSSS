package utils.scenes.ui

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.CanvasLayer
import godot.api.Control
import godot.api.InputEvent
import godot.api.Node
import godot.api.PackedScene
import godot.extension.getNodeAs
import godot.global.GD

@RegisterClass
class PauseManager : Node() {

    companion object {
        val PAUSE_MENU = GD.load<PackedScene>("res://src/utils/scenes/ui/pause_menu.tscn")
    }

    @RegisterProperty
    var isInStage: Boolean = false

    @RegisterProperty
    var isPaused: Boolean = false

    private var menu: Control? = null

    @RegisterFunction
    override fun _ready() {
        processMode = ProcessMode.ALWAYS
    }

    @RegisterFunction
    override fun _input(event: InputEvent?) {
        if (!isInStage) {
            return
        }

        event?.let {
            if (it.isActionPressed("pause")) {

                isPaused = !isPaused

                if (isPaused) {
                    pauseGame()
                } else {
                    resumeGame()
                }
            }
        }
    }

    @RegisterFunction
    fun pauseGame() {
        if (menu == null) {
            instantiateMenu()
        }

        getTree()?.paused = true
        menu?.show()
    }

    @RegisterFunction
    fun resumeGame() {
        if (menu == null) {
            instantiateMenu()
        }

        getTree()?.paused = false
        menu?.hide()
    }

    private fun instantiateMenu() {
        val pauseMenu = PAUSE_MENU!!.instantiate() as Control
        pauseMenu.hide()

        val canvasLayer = this.getParent()?.getNodeAs<CanvasLayer>("%CanvasLayer")

        if (canvasLayer != null) {
            canvasLayer.addChild(pauseMenu)
        } else {
            getTree()?.currentScene?.addChild(pauseMenu)
        }

        this.menu = pauseMenu

    }
}