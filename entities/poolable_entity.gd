class_name PoolableEntity
extends Entity

signal activated
signal deactivated

@export var pool: ObjectPool

func deactive() -> void:
	self.visible = false
	deactivated.emit()


func active() -> void:
	self.visible = true
	activated.emit()


func reset() -> void:
	pass
