class_name Component
extends Node

var _active := true

@export var active: bool = true:
	get:
		return _active
	set(value):
		_active = value
		set_process(value)
		set_physics_process(value)

@export var entity: Entity

func _ready() -> void:
	if not Engine.is_editor_hint() and entity == null:
		assert(false, "[Component] Entity is required")
		
	active = active


func activate() -> void:
	active = true


func deactivate() -> void:
	active = false
