extends Node

const PAUSE_MENU = preload("res://utils/scenes/ui/pause_menu.tscn")

var is_in_stage: bool = false
var is_paused: bool = false
var menu: Control

func _ready() -> void:
	self.process_mode = Node.PROCESS_MODE_ALWAYS


func _input(event: InputEvent) -> void:
	if not is_in_stage:
		return
	
	if event.is_action_pressed("pause"):
		is_paused = !is_paused
		
		if is_paused:
			pause_game()
		else:
			resume_game()


func pause_game() -> void:
	if not menu:
		_instantiate_menu()
	
	get_tree().paused = true
	menu.show()


func resume_game() -> void:
	if not menu:
		_instantiate_menu()
	
	get_tree().paused = false
	menu.hide()


func _instantiate_menu() -> void:
	menu = PAUSE_MENU.instantiate()
	menu.hide()
	get_tree().current_scene.add_child(menu)
