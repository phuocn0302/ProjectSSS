class_name FrameFreezeComponent
extends Component

@export var freeze_duration: float = 0.3
@export_range(0.0, 1.0) var freeze_amount: float = 0.3

func _exit_tree() -> void:
	if Engine.time_scale < 1.0:
		Engine.time_scale = 1.0


func freeze(_args: Variant) -> void:
	Engine.time_scale = freeze_amount
	await get_tree().create_timer(freeze_duration, true, false, true).timeout
	Engine.time_scale = 1
