extends Control

signal no_pressed
signal yes_pressed

@onready var bg: ColorRect = $BG
@onready var dialog_control: Control = $DialogControl
@onready var you_died: Label = $YouDied
@onready var no_button: TextureButton = $DialogControl/NoButton
@onready var yes_button: TextureButton = $DialogControl/YesButton


func _ready() -> void:
	var default_bg_a = bg.modulate.a
	
	bg.modulate.a = 0
	dialog_control.modulate.a = 0
	you_died.modulate.a = 0
	
	var tween = create_tween().set_parallel(true)
	tween.tween_property(bg, "modulate:a", default_bg_a, 1)
	tween.tween_property(you_died, "modulate:a", 1, 1)
	tween.chain()
	tween.tween_property(dialog_control, "modulate:a", 1, 0.5)
	
	no_button.mouse_entered.connect(func(): no_button.grab_focus())
	yes_button.mouse_entered.connect(func(): yes_button.grab_focus())
	
	yes_button.grab_focus()

func _on_no_button_pressed(free: bool = true) -> void:
	no_pressed.emit()
	if free:
		_self_free()


func _on_yes_button_pressed(free: bool = true) -> void:
	yes_pressed.emit()
	if free:
		_self_free()


func _self_free() -> void:
	var tween = create_tween()
	tween.tween_property(self, "modulate:a", 0, 0.1)
	
	await tween.finished
	
	queue_free()
