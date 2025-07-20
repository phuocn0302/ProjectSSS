class_name ComponentArea2D
extends Area2D

var _active := true

@export var active: bool = true:
	get:
		return _active
	set(value):
		_active = value
		self.set_deferred("monitoring", value)
		self.set_deferred("monitorable", value)

@export var entity: Entity

func _ready() -> void:
	if not Engine.is_editor_hint() and entity == null:
		assert(false, "[ComponentArea2D] Entity is required")
	
	self.area_entered.connect(on_area_entered)
	self.area_exited.connect(on_area_exit)


func on_area_entered(_area: Area2D) -> void:
	pass


func on_area_exit(_area: Area2D) -> void:
	pass
