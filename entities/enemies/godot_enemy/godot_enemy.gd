class_name GodotEnemy
extends Enemy

signal defeated

func _on_health_component_health_reached_zero() -> void:
	defeated.emit()
