extends CapyBossState

@export var max_number_of_sweep: int = 4
@export var min_number_of_sweep: int = 2
@export_range(0,1) var continue_sweep_chance: float = 0.6
@export var sweep_time: float = 1
@export var forward_offset: float = 20
@export var move_to_edge_time: float = 0.5
@export var delay_before_sweep: float = 1

var _edge_location: Array[Vector2]
var _location_index: int = 0
var _sweep_counter: int = 0
var _move_tween: Tween

@onready var gun: AnimatedSprite2D = $"../../Gun"
@onready var laser_emitter: LaserEmitter = $"../../Spawners/LaserEmitter"
@onready var circle_projectile_spawner: CircleProjectileSpawner = $"../../Spawners/CircleProjectileSpawner"
@onready var move_warning_line: MoveWarningLine = $"../../MoveWarningLine"

func enter() -> void:
	gun.play("to_ray_gun")
	
	_edge_location = [
		Vector2(0, boss.global_position.y + forward_offset),
		Vector2(180, boss.global_position.y + forward_offset)
	]
	
	_sweep_counter = 0
	
	_setup_laser_emitter()
	_setup_circle_projectile_spawner()
	
	await _move_to_edge()
	
	_sweep()


func exit() -> void:
	gun.play("back_to_gun")
	
	_move_tween = create_tween()
	_move_tween.tween_property(boss, "global_position", boss.default_position, 0.5)


func _sweep() -> void:
	_sweep_counter += 1
	_location_index = posmod(_location_index + 1, 2)
	
	
	move_warning_line.direction = boss.global_position.direction_to(_edge_location[_location_index])
	
	move_warning_line.show()
	
	await get_tree().create_timer(delay_before_sweep).timeout
	
	move_warning_line.hide()
	_shoot()
	
	_move_tween = create_tween()
	_move_tween.tween_property(
		boss,
		"global_position",
		_edge_location[_location_index],
		sweep_time
	)
	
	_move_tween.finished.connect(_on_sweep_end)


func _on_sweep_end() -> void:
	circle_projectile_spawner.active = false
	
	var _continue_sweep: bool = false
	
	if _sweep_counter < min_number_of_sweep:
		_continue_sweep = true
	else:
		_continue_sweep = randf() < continue_sweep_chance
	
	await get_tree().create_timer(delay_before_sweep).timeout
	
	if _continue_sweep and _sweep_counter < max_number_of_sweep:
		_sweep()
	else:
		state_machine.change_state(states["ChaseLaser"])


func _shoot() -> void:
	circle_projectile_spawner.active = true
	boss.recoil_gun()
	laser_emitter.emit_laser()


func _move_to_edge() -> void:
	_move_tween = create_tween()
	_move_tween.set_ease(Tween.EASE_IN_OUT).set_trans(Tween.TRANS_EXPO)
	
	_move_tween.tween_property(
		boss, 
		"global_position", 
		_edge_location[_location_index],
		move_to_edge_time
		)
	
	await _move_tween.finished


func _setup_laser_emitter() -> void:
	var laser_stats = LaserStats.new()
	laser_stats.emit_time = sweep_time
	laser_stats.cast_time = 0
	
	laser_emitter.laser_stats = laser_stats


func _setup_circle_projectile_spawner() -> void:
	circle_projectile_spawner.number_of_projectile = 10
	circle_projectile_spawner.rotate_pattern = true
	circle_projectile_spawner.rotate_speed  = 10
	circle_projectile_spawner.spawn_interval = 0.1
