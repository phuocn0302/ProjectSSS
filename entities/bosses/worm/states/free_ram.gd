extends WormBossState

signal went_to_start_pos

@export var number_of_ram: int = 5
@export var speed: float = 300
@export var go_up_duration: float = 1
@export var going_back_pos: Vector2 = Vector2(90,0)

@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var screen_shake_component: ScreenShakeComponent = $"../../Components/ScreenShakeComponent"

var _tween: Tween
var _ram_counter: int = 0
var _is_ramming: bool = false
var _is_going_to_start_pos: bool = false
var _is_attack_dir_set: bool = false

# This for going back
var _direction: Vector2
var _warning_line: MoveWarningLine

var _current_speed: float
var _is_ram_from_left: bool = true
var _ram_from_pos: Vector2
var _ram_to_pos: Vector2

func enter() -> void:
	randomize()
	_ram_counter = 0
	idle_move_component.active = false
	
	if not _warning_line:
		_warning_line = MoveWarningLine.new()
		get_tree().current_scene.add_child(_warning_line)
	
	boss.segments[0].shoot_trigger.screen_entered.connect(_shoot)
	
	await _go_up()
	_attack()


func exit() -> void:
	if boss.segments[0].shoot_trigger.screen_entered.is_connected(_shoot):
		boss.segments[0].shoot_trigger.screen_entered.disconnect(_shoot)
	
	_is_ramming = false
	_is_going_to_start_pos = false
	_is_attack_dir_set = false
	_warning_line.hide()
	idle_move_component.active = true


func process_physics(delta: float) -> void:
	if not _is_ramming:
		return
	
	_current_speed += 100 * delta
	
	boss.global_position += _direction * _current_speed * delta


func _move_to_start_pos(delta: float) -> void:
	var target_dir = boss.global_position.direction_to(_ram_from_pos)
	var angle_to_target = _direction.angle_to(target_dir)
	
	var max_angle = deg_to_rad(80) * delta
	var clamped_angle = clamp(angle_to_target, -max_angle, max_angle)
	
	_direction = _direction.rotated(clamped_angle).normalized()
	
	if boss.global_position.distance_squared_to(_ram_from_pos) > 100:
		boss.global_position += _direction * speed * delta
		boss.global_rotation = _direction.angle() - PI/2 
	else:
		_is_going_to_start_pos = false
		went_to_start_pos.emit()


func _attack() -> void:
	_ram_counter += 1
	
	_make_path()
	# Tp boss to form pos with offfset
	_direction = _ram_from_pos.direction_to(_ram_to_pos)
	boss.global_rotation = _direction.angle() - PI/2
	boss.global_position = _ram_from_pos + (- _direction * 200)
	
	_warning_line.show()
	
	_current_speed = speed
	await Utils.create_timer(1).timeout

	_warning_line.hide()
	_is_ramming = true
	screen_shake_component.shake()
	
	await Utils.create_timer(3).timeout
	
	_is_ramming = false
	
	if _ram_counter < number_of_ram:
		_attack()
	else:
		boss.segments[0].shoot_trigger.screen_entered.disconnect(_shoot)
		_tween = create_tween().set_parallel(true)
		_tween.tween_property(boss, "global_rotation", 0, 0.5)
		_tween.tween_property(boss, "global_position", Vector2(90, -50), 1)
		
		_tween.chain()
		
		_tween.tween_property(boss, "global_position", boss.default_position, 1)
		await _tween.finished
		
		state_machine.change_state(states["Idle"])


func _go_up() -> void:
	var go_up_offset = boss.global_position + Vector2.UP * 200
	
	_tween = create_tween().set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	_tween.tween_property(
		boss, 
		"global_position", 
		go_up_offset,
		go_up_duration
	)
	
	await _tween.finished


func _make_path() -> void:
	_ram_from_pos = Vector2(0, randi_range(0, 256))
	_ram_to_pos = Vector2(0, randi_range(0, 256))
	
	if _is_ram_from_left:
		_ram_to_pos.x = 180
	else: 
		_ram_from_pos.x = 180
	
	if boss.player and randf() >= 0.5:
		_ram_to_pos.y = boss.player.global_position.y
	
	_is_ram_from_left = !_is_ram_from_left
	
	_warning_line.global_position = _ram_from_pos
	_warning_line.direction = _ram_from_pos.direction_to(_ram_to_pos)


func _shoot() -> void:
	var end_time = Time.get_ticks_msec() + 2000 
	while Time.get_ticks_msec() < end_time:
		for s in boss.segments:
			s.target_projectile_spawner.spawn()
		await Utils.create_timer(0.3).timeout
