extends Node2D

const QUIT_GAME_CONFIRM = preload("res://utils/scenes/ui/quit_game_confirm.tscn")
const DEMO_BOSS_SELECTOR = preload("res://stages/demo_boss_selector/demo_boss_selector.tscn")

@export var button_move_amount: int = 10
@export var hover_anim_time: float = 0.2

var _buttons_default_location: Dictionary[TextureButton, Vector2]

@onready var animation_player: AnimationPlayer = $AnimationPlayer
@onready var start_button: TextureButton = %StartButton
@onready var options_button: TextureButton = %OptionsButton
@onready var quit_button: TextureButton = %QuitButton
@onready var buttons: Control = $Buttons

func _ready() -> void:
	pass


## AnimationPlayer call this on end
func _setup_button() -> void:
	var texture_buttons := []
	
	for b in buttons.get_children():
		if b is TextureButton:
			b.disabled = false
			b.focus_mode = Control.FOCUS_ALL
			_buttons_default_location[b as TextureButton] = b.position
			
			b.mouse_entered.connect(
				func():
					b.grab_focus()
					_hover_button(b)
			)
			b.mouse_exited.connect(Callable(self, "_unhover_button").bind(b)) 
			
			b.focus_entered.connect(Callable(self, "_hover_button").bind(b))
			b.focus_exited.connect(Callable(self, "_unhover_button").bind(b)) 
			
			texture_buttons.append(b)
	
	start_button.grab_focus()


func _hover_button(button: TextureButton) -> void:
	var tween := create_tween()
	
	tween.tween_property(
		button,
		"position", 
		button.position + Vector2.LEFT * button_move_amount,
		hover_anim_time
		)


func _unhover_button(button: TextureButton) -> void:
	var tween := create_tween()
	
	tween.tween_property(
		button,
		"position", 
		_buttons_default_location[button],
		hover_anim_time
		)


func _on_start_button_pressed() -> void:
	start_button.release_focus()
	
	var demo_boss_selector = DEMO_BOSS_SELECTOR.instantiate()
	get_tree().current_scene.add_child(demo_boss_selector)
	
	demo_boss_selector.tree_exited.connect(
		func(): 
			if self.is_visible_in_tree():
				start_button.grab_focus()
	)


func _on_options_button_pressed() -> void:
	pass


func _on_quit_button_pressed() -> void:
	quit_button.release_focus()
	
	var confirm_dialog = QUIT_GAME_CONFIRM.instantiate()
	get_tree().current_scene.add_child(confirm_dialog)
	
	confirm_dialog.tree_exited.connect(
		func(): quit_button.grab_focus()
	)
