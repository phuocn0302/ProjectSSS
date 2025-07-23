class_name ObjectPool
extends Node

@export var object_to_pool: PackedScene

var available_pool: Array[PoolableEntity] = []
var is_initialized := false

func setup(_object_to_pool: PackedScene):
	if is_initialized:
		return
	is_initialized = true
	object_to_pool = _object_to_pool


func _add_to_pool() -> PoolableEntity:
	var obj: PoolableEntity
	
	obj = object_to_pool.instantiate() as PoolableEntity
	assert(obj is PoolableEntity)
	
	obj.pool = self
	add_child(obj)
	
	return obj


func get_instance() -> PoolableEntity:
	var obj: PoolableEntity
	
	if available_pool.is_empty():
		obj = _add_to_pool()
	else:
		obj = available_pool.pop_back()
	
	obj.active()
	return obj


func return_instance(obj: PoolableEntity) -> void:
	available_pool.push_back(obj)
