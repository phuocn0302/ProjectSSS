extends Control

@export var scene_to_load: PackedScene
@export var duration: float = 1
func _ready() -> void:
	assert(scene_to_load)
	
	self.modulate.a = 0
	var tween = create_tween()
	tween.tween_property(self, "modulate:a", 1, 0.5)
	tween.tween_interval(duration)
	tween.tween_property(self, "modulate:a", 0, 0.5)
	
	await tween.finished
	
	get_tree().change_scene_to_packed(scene_to_load)
