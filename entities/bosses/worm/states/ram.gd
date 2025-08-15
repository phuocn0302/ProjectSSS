extends WormBossState

signal went_back

@export var wind_up_amount: float = 30
@export var wind_up_duration: float = 1
@export var ram_distance: float = 400
@export var ram_duration: float = 0.5
@export var ram_delay: float = 0.6
@export var going_back_pos: Vector2 = Vector2(90,0)

@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var screen_shake_component: ScreenShakeComponent = $"../../Components/ScreenShakeComponent"

var _tween: Tween
var _dir_to_player: Vector2
var _ram_to_pos: Vector2
var _is_ramming: bool = false
var _is_going_back: bool = false
var _is_attack_dir_set: bool = false
var _attack_dir: Vector2
# This for going back
var _direction: Vector2
var _warning_line: MoveWarningLine

func enter() -> void:
	_attack_dir = Vector2.DOWN
	idle_move_component.active = false
	
	if not _warning_line:
		_warning_line = MoveWarningLine.new()
		get_tree().current_scene.add_child.call_deferred(_warning_line)
	
	_attack()


func exit() -> void:
	_is_ramming = false
	_is_going_back = false
	_is_attack_dir_set = false
	_warning_line.hide()
	idle_move_component.active = true


func process_frame(_delta: float) -> void:
	if not boss.player:
		_dir_to_player = Vector2.DOWN
		return
	
	_dir_to_player = boss.global_position.direction_to(boss.player.global_position)
	
	if not _is_attack_dir_set:
		boss.global_rotation = _dir_to_player.angle() - PI/2


func process_physics(delta: float) -> void:
	if not _is_going_back:
		return
	
	var target_dir = boss.global_position.direction_to(going_back_pos)
	var angle_to_target = _direction.angle_to(target_dir)
	
	var max_angle = deg_to_rad(80) * delta
	var clamped_angle = clamp(angle_to_target, -max_angle, max_angle)
	
	_direction = _direction.rotated(clamped_angle).normalized()
	
	if boss.global_position.distance_squared_to(going_back_pos) > 100:
		boss.global_position += _direction * 500 * delta
		boss.global_rotation = _direction.angle() - PI/2 
	else:
		_is_going_back = false
		went_back.emit()


func _attack() -> void:
	await _wind_up()
	await _ram()
	_is_going_back = true
	await went_back
	
	_tween = create_tween().set_parallel(true)
	_tween.tween_property(boss, "global_rotation", 0, 0.5)
	_tween.tween_property(boss, "global_position", boss.default_position, 1)
	await _tween.finished
	
	state_machine.change_state(states["Idle"])


func _wind_up() -> void:
	screen_shake_component.shake()
	var wind_up_offset = boss.global_position + Vector2.UP * wind_up_amount
	
	_tween = create_tween().set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	_tween.tween_property(
		boss, 
		"global_position", 
		wind_up_offset,
		wind_up_duration
	)
	
	await _tween.finished
	
	_warning_line.global_position = boss.global_position
	_warning_line.direction = _dir_to_player
	_warning_line.show_time = 0.2
	_warning_line.show()
	
	_attack_dir = _dir_to_player
	_is_attack_dir_set = true
	
	await Utils.create_timer(ram_delay).timeout


func _ram() -> void:
	_is_ramming = true
	_ram_to_pos = boss.global_position + _attack_dir * ram_distance
	_direction = _attack_dir
	
	_tween = create_tween().set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	_tween.tween_property(
		boss, 
		"global_position", 
		_ram_to_pos,
		ram_duration
	)
	
	await _tween.finished
	_warning_line.hide()
	for b in boss.segments:
		b.circle_projectile_spawner.spawn()
