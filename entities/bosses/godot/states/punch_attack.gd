extends GodotBossState

@export var follow_speed: float = 120
@export var player_offset: float = 100
@export var attack_delay: float = 3
@export var punch_prepare_time: float = 0.4
@export var punch_time: float = 0.2
@export var arm_return_time: float = 1
@export var punch_distance: float = 150
@export var wind_up_offset: float = 30

var _attack_arm: GodotArm
var _arm_index: int = 0
var _arm_original_pos: Vector2
var _timer: SceneTreeTimer
var _tween: Tween

@onready var right_arm_pos: Marker2D = $"../../Arms/RightArmPos"
@onready var left_arm_pos: Marker2D = $"../../Arms/LeftArmPos"

func enter() -> void:
	boss.circle_projectile_spawner.active = true
	
	_arm_index = posmod(_arm_index + 1, 2)
	_attack_arm = [boss.left_arm, boss.right_arm][_arm_index]
	
	_arm_original_pos = [
		left_arm_pos.global_position,
		right_arm_pos.global_position
		][_arm_index]
	
	_timer = get_tree().create_timer(attack_delay)
	_timer.timeout.connect(_attack)


func process_frame(delta: float) -> void:
	if _timer.time_left > 0:
		_follow_player(delta)


func exit() -> void:
	boss.circle_projectile_spawner.active = false
	_timer.timeout.disconnect(_attack)
	if _tween: _tween.kill()


func _follow_player(delta: float) -> void:
	if not boss.player: 
		return
	var player := boss.player as Player
	
	var offset = Vector2(
		player.global_position.x,
		player.global_position.y - player_offset
	)
	
	_attack_arm.global_position = _attack_arm.global_position.move_toward(
		offset,
		follow_speed * delta)


func _attack() -> void:
	var start_pos := _attack_arm.position
	var prepare_offset := Vector2.UP * wind_up_offset
	var punch_offset := Vector2.DOWN * punch_distance
	
	var prepare_pos := start_pos + prepare_offset
	var punch_pos := prepare_pos + punch_offset
	
	_attack_arm.ghost_trailing_component.active = true
	
	_tween = create_tween()
	
	_tween.set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	_tween.tween_property(_attack_arm, "position", prepare_pos, punch_prepare_time)
	
	_tween.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_IN)
	_tween.tween_property(_attack_arm, "position", punch_pos, punch_time)
	
	_tween.tween_callback(Callable(_attack_arm, "explode"))
	
	_tween.tween_property(_attack_arm, "global_position", _arm_original_pos, arm_return_time)
	
	await _tween.finished
	_attack_arm.ghost_trailing_component.active = false
	
	state_machine.change_state(states["Idle"])
