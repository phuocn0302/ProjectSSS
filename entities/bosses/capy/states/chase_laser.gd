extends CapyBossState

@export var number_of_shot: int = 4
@export var chase_duration: float = 3
@export var move_speed: float = 15

var _laser_stats: LaserStats
var _shoot_timer: SceneTreeTimer
var _is_shooting: bool = false
var _shot_counter: int = 0

@onready var left_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner"
@onready var right_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner2"
@onready var laser_emitter: LaserEmitter = $"../../Spawners/LaserEmitter"
@onready var warning_line: Line2D = $"../../Spawners/LaserEmitter/WarningLine"
@onready var gun: AnimatedSprite2D = $"../../Gun"

func enter() -> void:
	gun.play("to_ray_gun")
	
	left_missile_spawner.spawn_interval = 3
	right_missile_spawner.spawn_interval = 3
	
	left_missile_spawner.active = true
	right_missile_spawner.active = true 
	
	warning_line.show()
	
	_setup_laser_emitter()
	
	_shoot_timer = Utils.create_timer(chase_duration)
	_shoot_timer.timeout.connect(_shoot)


func exit() -> void:
	gun.play("back_to_gun")
	
	warning_line.hide()
	
	left_missile_spawner.active = false
	right_missile_spawner.active = false
	
	_shoot_timer.timeout.disconnect(_shoot)
	
	var tween = create_tween()
	tween.tween_property(boss, "global_position", boss.default_position, 0.5)


func process_physics(delta: float) -> void:
	if not boss.player:
		return
	
	if not warning_line.visible:
		warning_line.show()
	
	if _shoot_timer.time_left < chase_duration * 0.1:
		_is_shooting = true
		warning_line.hide()
	
	
	var _move_speed = move_speed * delta
	
	if _is_shooting:
		_move_speed = (move_speed / 8) * delta
	
	boss.global_position.x = lerp(
		boss.global_position.x,
		boss.player.global_position.x,
		_move_speed
	)


func _shoot() -> void:
	boss.recoil_gun()
	laser_emitter.emit_laser()
	
	var total_laser_time = _laser_stats.cast_time + _laser_stats.decay_time + _laser_stats.emit_time
	await Utils.create_timer(total_laser_time).timeout
	
	_is_shooting = false
	_shot_counter += 1
	
	if _shot_counter >= number_of_shot:
		state_machine.change_state(states["Idle"])
		return
	
	_shoot_timer = Utils.create_timer(chase_duration)
	_shoot_timer.timeout.connect(_shoot)


func _setup_laser_emitter() -> void:
	_laser_stats = LaserStats.new()
	laser_emitter.laser_stats = _laser_stats
