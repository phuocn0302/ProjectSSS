class_name PoolableEntity
extends Entity

signal activated
signal deactivated

@export var pool: ObjectPool

func deactive() -> void:
	self.visible = false
	set_process(false)
	set_physics_process(false)
	
	pool.return_instance(self)
	deactivated.emit()


func active() -> void:
	self.visible = true
	set_process(true)
	set_physics_process(true)
	activated.emit()


func reset() -> void:
	pass
