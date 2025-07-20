class_name MoveComponent
extends Component

enum MoveType {
	STRAIGHT,
	SINEWAVE
}

@export var move_type: MoveType = MoveType.STRAIGHT
@export var direction: Vector2 = Vector2.DOWN
@export var speed: float = 70

@export_category("Sinewave")
## Max distance from center
@export var sine_amp: float = 5
## Full cycles per second
@export var sine_freq: float = 5

var _sine_t: float = 0
var _starting_pos: Vector2 = Vector2.ZERO

func _ready() -> void:
	super._ready()
	_starting_pos = entity.global_position
	direction = direction.normalized()


func _physics_process(delta: float) -> void:
	match move_type:
		MoveType.STRAIGHT:
			straight_move(delta)
		MoveType.SINEWAVE:
			sinewave_move(delta)


func straight_move(delta: float) -> void:
	entity.global_position += direction.normalized() * speed * delta


func sinewave_move(delta: float) -> void:
	_sine_t = fposmod(_sine_t + delta, TAU)

	var forward_offset = direction * speed * _sine_t

	var perp = Vector2(-direction.y, direction.x)
	var sine_offset = perp * (sin(_sine_t * sine_freq) * sine_amp)

	entity.global_position = _starting_pos + forward_offset + sine_offset
