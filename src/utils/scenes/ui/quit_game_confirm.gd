extends Control

@onready var no_button: TextureButton = $NoButton
@onready var yes_button: TextureButton = $YesButton

func _ready() -> void:
	no_button.grab_focus()
	
	no_button.focus_neighbor_right = yes_button.get_path()
	yes_button.focus_neighbor_left = no_button.get_path()


func _input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel"):
		_on_no_button_pressed()


func _on_no_button_pressed() -> void:
	var tween = create_tween()
	tween.tween_property(self, "modulate:a", 0, 0.1)
	
	await tween.finished
	self.queue_free()


func _on_yes_button_pressed() -> void:
	get_tree().quit()
