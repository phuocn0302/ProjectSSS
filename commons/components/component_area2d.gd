class_name ComponentArea2D
extends Area2D

@export var active: bool = true: set = _set_active
@export var entity: Entity

func _ready() -> void:
	self.area_entered.connect(on_area_entered)
	self.area_exited.connect(on_area_exit)


func on_area_entered(_area: Area2D) -> void:
	pass


func on_area_exit(_area: Area2D) -> void:
	pass


func _set_active(val: bool) -> void:
	active = val
	
	self.set_deferred("monitoring", val)
	self.set_deferred("monitorable", val)


func activate() -> void:
	active = true


func deactivate() -> void:
	active = false
