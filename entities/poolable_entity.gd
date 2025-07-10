class_name PoolableEntity
extends Entity

@export var pool: ObjectPool

func deactive() -> void:
	self.visible = false


func active() -> void:
	self.visible = true


func reset() -> void:
	pass
