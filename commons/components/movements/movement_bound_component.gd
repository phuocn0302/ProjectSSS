class_name MovementBoundsComponent
extends Component

## Origin and (Width, Height)
@export var bounds: Array[Vector2] = [Vector2.ZERO, Vector2(180, 320)]


func _physics_process(_delta: float) -> void:
	entity.global_position.x = clamp(entity.global_position.x, bounds[0].x, bounds[1].x)
	entity.global_position.y = clamp(entity.global_position.y, bounds[0].y, bounds[1].y)
