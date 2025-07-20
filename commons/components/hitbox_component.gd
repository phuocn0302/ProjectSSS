class_name HitboxComponent
extends ComponentArea2D

@export var damage: float = 1
@export var queue_free_on_contact: bool = false
@export var queue_free_sfx: PackedScene

signal hit

func on_area_entered(area: Area2D) -> void:
	if area is HurtboxComponent:
		area.take_damage(damage)
		hit.emit()
	
	if queue_free_on_contact:
		if queue_free_sfx:
			var sfx = queue_free_sfx.instantiate()
			get_tree().current_scene.add_child(sfx)
		entity.queue_free()
