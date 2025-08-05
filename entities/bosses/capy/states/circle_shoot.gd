extends CapyBossState


@export var duration: float = 5
@export var burst_time: float = 0.5
@export var burst_delay: float = 0.5

var missile_spawners: Array[ProjectileSpawner]

var _timer: SceneTreeTimer
var _burst_timer: SceneTreeTimer
var _burst_delay_timer: SceneTreeTimer
var _missle_index = 0

@onready var circle_projectile_spawner: CircleProjectileSpawner = $"../../Spawners/CircleProjectileSpawner"
@onready var left_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner"
@onready var right_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner2"

func enter() -> void:
	assert(burst_time + burst_delay < duration)
	assert(fposmod(duration, burst_time + burst_delay) == 0)
	
	_setup_circle_projectile_spawner()
	
	missile_spawners = [left_missile_spawner, right_missile_spawner]
	
	_timer = get_tree().create_timer(duration)
	_timer.timeout.connect(_on_timer_timeout)
	_shoot()


func exit() -> void:
	circle_projectile_spawner.active = false
	_timer.disconnect("timeout", _on_timer_timeout)
	_burst_delay_timer.disconnect("timeout", _shoot)


func _shoot() -> void:
	missile_spawners[_missle_index].spawn()
	
	_missle_index = posmod(_missle_index + 1, 2)
	
	circle_projectile_spawner.active = true
	circle_projectile_spawner.rotate_speed *= -1
	
	_burst_timer = get_tree().create_timer(burst_time)
	await _burst_timer.timeout
	
	if not circle_projectile_spawner.active:
		return 
	
	circle_projectile_spawner.active = false
	
	_burst_delay_timer = get_tree().create_timer(burst_delay)
	_burst_delay_timer.timeout.connect(_shoot)


func _setup_circle_projectile_spawner() -> void:
	circle_projectile_spawner.number_of_projectile = 15
	circle_projectile_spawner.rotate_pattern = true
	circle_projectile_spawner.rotate_speed  = 10
	circle_projectile_spawner.spawn_interval = 0.2


func _on_timer_timeout() -> void:
	circle_projectile_spawner.active = false
	state_machine.change_state(states["Idle"])
