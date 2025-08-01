class_name ComponentArea2D
extends Area2D

var _active := true

@export var active: bool = true:
	get:
		return _active
	set(value):
		_active = value
		if value:
			activate()
		else:
			deactivate()


@export var entity: Entity

func _ready() -> void:
	self.area_entered.connect(on_area_entered)
	self.area_exited.connect(on_area_exit)


func on_area_entered(_area: Area2D) -> void:
	pass


func on_area_exit(_area: Area2D) -> void:
	pass


func activate() -> void:
	self.set_deferred("monitoring", true)
	self.set_deferred("monitorable", true)


func deactivate() -> void:
	self.set_deferred("monitoring", false)
	self.set_deferred("monitorable", false)
