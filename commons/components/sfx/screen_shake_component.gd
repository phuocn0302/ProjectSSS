class_name ScreenShakeComponent
extends Component

@export var shake_amount: float = 3
@export var shake_duration: float = 0.2

var _camera: Camera2D
var _shake_t: float = 0
var _tween: Tween

func _process(_delta: float) -> void:
	_camera = get_viewport().get_camera_2d()
	
	if not _camera:
		return
	
	if _shake_t > 0:
		_camera.offset = _random_offset()
	else:
		_camera.offset = Vector2.ZERO


func _exit_tree() -> void:
	if _camera:
		_camera.offset = Vector2.ZERO


func shake() -> void:
	_shake_t = shake_amount
	
	_tween = create_tween()
	_tween.tween_property(self, "_shake_t", 0, shake_duration)


func _random_offset() -> Vector2:
	return Vector2(
		randf_range(- _shake_t, _shake_t),
		randf_range(- _shake_t, _shake_t)
	)
