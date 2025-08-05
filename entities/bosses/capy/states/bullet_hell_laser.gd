extends CapyBossState

@export var duration: float = 7
@export var shoot_delay: float = 1

var _timer: SceneTreeTimer

@onready var gun: AnimatedSprite2D = $"../../Gun"
@onready var laser_emitter: LaserEmitter = $"../../Spawners/LaserEmitter"
@onready var circle_projectile_spawner: CircleProjectileSpawner = $"../../Spawners/CircleProjectileSpawner"
@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var warning_line: Line2D = $"../../Spawners/LaserEmitter/WarningLine"


func enter() -> void:
	idle_move_component.active = false
	
	warning_line.show()
	gun.play("to_ray_gun")
	
	await gun.animation_finished
	
	_setup_circle_projectile_spawner()
	_setup_laser_emitter()
	
	await get_tree().create_timer(shoot_delay).timeout
	warning_line.hide()
	await get_tree().create_timer(0.3).timeout
	_start_shooting()


func exit() -> void:
	gun.play("back_to_gun")
	
	laser_emitter.force_stop_emit()
	circle_projectile_spawner.active = false
	idle_move_component.active = true
	
	var tween = create_tween()
	tween.tween_property(boss, "global_position", boss.default_position, 0.5)


func process_physics(delta: float) -> void:
	if not boss.player:
		return
	
	boss.global_position.x = lerp(
		boss.global_position.x,
		boss.player.global_position.x,
		delta 
	)


func _start_shooting() -> void:
	boss.recoil_gun()
	
	laser_emitter.emit_laser()
	circle_projectile_spawner.active = true
	
	_timer = get_tree().create_timer(duration)
	_timer.timeout.connect(_on_timer_timeout)


func _setup_laser_emitter() -> void:
	var laser_stats = LaserStats.new()
	laser_stats.emit_time = duration
	laser_stats.cast_time = 0.5
	
	laser_emitter.laser_stats = laser_stats


func _setup_circle_projectile_spawner() -> void:
	circle_projectile_spawner.number_of_projectile = 1
	circle_projectile_spawner.rotate_pattern = true
	circle_projectile_spawner.rotate_speed = 15
	circle_projectile_spawner.spawn_interval = 0.05


func _on_timer_timeout() -> void:
	state_machine.change_state(states["Idle"])
