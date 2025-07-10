class_name ObjectPool
extends Node

@export var pool_size: int = 20
@export var object_to_pool: PackedScene

var pool: Array = []

var is_initialized: bool = false

func _ready():
	if not is_initialized and object_to_pool and pool.is_empty():
		for i in pool_size:
			add_to_pool()

func setup(_object_to_pool: PackedScene, _pool_size: int = 20):
	if is_initialized:
		return
	is_initialized = true
	
	self.object_to_pool = _object_to_pool
	self.pool_size = _pool_size
	
	for i in pool_size:
		add_to_pool()

func get_instance() -> Node2D:
	for obj in pool:
		if not obj.visible:
			obj.active()
			return obj
	
	return add_to_pool()


func add_to_pool() -> Node2D:
	var object = object_to_pool.instantiate() as PoolableEntity
	
	if not object is PoolableEntity:
		push_error("ObjectPool: Pooled scene must extend from PoolableEntity.")
		return null

	object.deactive() 
	
	pool.append(object)
	pool_size = pool.size()
	self.add_child(object)
	return object
