package stages.title_screen

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Vector2
import godot.core.connect
import godot.core.dictionaryOf
import godot.extension.getNodeAs

@RegisterClass
class TitleScreenManager : Node2D() {

    @Export
    @RegisterProperty
    var quitGameConfirmationDialog: PackedScene? = null

    @Export
    @RegisterProperty
    var demoBossSelectorScreen: PackedScene? = null

    @Export
    @RegisterProperty
    var buttonHoverAmount: Int = 10

    @Export
    @RegisterProperty
    var hoverAnimTime: Double = 0.2

    private var buttonsDefaultLocation = dictionaryOf<TextureButton, Vector2>()

    lateinit var animPlayer: AnimationPlayer
    lateinit var startButton: TextureButton
    lateinit var optionsButton: TextureButton
    lateinit var quitButtons: TextureButton
    lateinit var buttons: Control

    @RegisterFunction
    override fun _ready() {
        animPlayer = getNodeAs("%AnimationPlayer")!!
        startButton = getNodeAs("%StartButton")!!
        optionsButton = getNodeAs("%OptionsButton")!!
        quitButtons = getNodeAs("%QuitButton")!!
        buttons = getNodeAs("%Buttons")!!

        startButton.pressed.connect {
            demoBossSelectorScreen?.let {
                val ins = it.instantiate()
                getTree()?.currentScene?.addChild(ins)

                ins?.treeExited?.connect {
                    startButton.grabFocus()
                }
            }
        }

        quitButtons.pressed.connect {
            quitGameConfirmationDialog?.let {
                val ins = it.instantiate()
                getTree()?.currentScene?.addChild(ins)

                ins?.treeExited?.connect {
                    quitButtons.grabFocus()
                }
            }
        }
    }

    @RegisterFunction
    fun setupButton() {
        for (b in buttons.getChildren().filterIsInstance<TextureButton>()) {
            b.disabled = false
            b.focusMode = Control.FocusMode.ALL

            buttonsDefaultLocation[b] = b.position

            b.mouseEntered.connect {
                b.grabFocus()
                hoverButton(b)
            }

            b.mouseExited.connect {
                unhoverButton(b)
            }

            b.focusEntered.connect {
                hoverButton(b)
            }

            b.focusExited.connect {
                unhoverButton(b)
            }
        }

        startButton.grabFocus()
    }

    private fun hoverButton(button: TextureButton) {
        val tween = createTween()

        tween?.tweenProperty(
            button,
            "position",
            button.position + Vector2.LEFT * buttonHoverAmount,
            hoverAnimTime
        )
    }

    private fun unhoverButton(button: TextureButton) {
        val tween = createTween()

        tween?.tweenProperty(
            button,
            "position",
            buttonsDefaultLocation[button],
            hoverAnimTime
        )
    }

}