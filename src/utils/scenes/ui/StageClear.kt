package utils.scenes.ui

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.TextureButton
import godot.api.Control
import godot.api.Label
import godot.api.PackedScene
import godot.core.Callable
import godot.core.toGodotName
import godot.extension.getNodeAs

@RegisterClass
class StageClear : Control() {

    @Export
    @RegisterProperty
    var nextStage: PackedScene? = null

    private lateinit var scoreLabel: Label
    private lateinit var yesButton: TextureButton
    private lateinit var noButton: TextureButton

    private var score: Int = -1

    @RegisterFunction
    override fun _ready() {
        scoreLabel = getNodeAs("%ScoreLabel")!!
        yesButton = getNodeAs("%YesButton")!!
        noButton = getNodeAs("%NoButton")!!
        scoreLabel.text = "Score: $score"

        yesButton.pressed.connect(Callable(this, "onYes".toGodotName()))
        noButton.pressed.connect(Callable(this, "onNo".toGodotName()))
    }

    @RegisterFunction
    fun setScore(score: Int) {
        this.score = score
    }

    @RegisterFunction
    fun onYes() {
        val tree = getTree()
        if (nextStage != null) {
            val newStage = nextStage?.instantiate()
            tree?.currentScene?.queueFree()
            tree?.root?.addChild(newStage!!)
            tree?.currentScene = newStage
        } else {
            tree?.changeSceneToFile("res://src/stages/title_screen/title_screen.tscn")
        }
    }

    @RegisterFunction
    fun onNo() {
        getTree()?.changeSceneToFile("res://src/stages/title_screen/title_screen.tscn")
    }
}


