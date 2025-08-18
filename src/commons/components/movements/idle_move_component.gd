class_name IdleMoveComponent
extends Component

enum MoveType {
	CIRCLE,
	FIGURE8,
	OSCILLATE,
}

@export var move_type: MoveType = MoveType.CIRCLE
@export var direction: Vector2 = Vector2.LEFT
@export var angular_speed: float = 1.0

@export_group("Circle Settings")
@export var circle_radius: float = 1.0

@export_group("Figure 8 Settings")
@export var figure8_amplitude: float = 1.0

@export_group("Oscillate Settings")
@export var osc_speed: float = 1.0
@export var osc_range: float = 50

var _t: float = 0.0
var _prev_offset: Vector2 = Vector2.ZERO

func _ready() -> void:
	super._ready()
	direction = direction.normalized()


func _physics_process(delta: float) -> void:
	_t = fposmod(_t + angular_speed * delta, TAU)

	match move_type:
		MoveType.CIRCLE:
			_circle_move()
		MoveType.FIGURE8:
			_figure8_move()
		MoveType.OSCILLATE:
			_oscillate_move()


func _circle_move() -> void:
	var local_offset = Vector2(
		circle_radius * cos(_t),
		circle_radius * sin(_t)
	)
	var rotated_offset = local_offset.rotated(direction.angle())
	var delta_offset = rotated_offset - _prev_offset
	entity.global_position += delta_offset
	_prev_offset = rotated_offset


func _figure8_move() -> void:
	var a = figure8_amplitude
	var local_offset = Vector2(
		a * cos(_t),
		a * sin(_t) * cos(_t)
	)
	var rotated_offset = local_offset.rotated(direction.angle())
	var delta_offset = rotated_offset - _prev_offset
	entity.global_position += delta_offset
	_prev_offset = rotated_offset


func _oscillate_move() -> void:
	var local_offset = direction * (osc_range * sin(_t * osc_speed))
	var delta_offset = local_offset - _prev_offset
	entity.global_position += delta_offset
	_prev_offset = local_offset
