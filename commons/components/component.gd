class_name Component
extends Node

var _active := true

@export var active: bool = true:
	get:
		return _active
	set(value):
		_active = value
		if _active:
			activate()
		else:
			deactivate()

@export var entity: Entity

func _ready() -> void:
	call_deferred("_apply_active_state")


func activate() -> void:
	set_process(true)
	set_physics_process(true)


func _apply_active_state() -> void:
	active = active

func deactivate() -> void:
	set_process(false)
	set_physics_process(false)
