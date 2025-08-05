extends CapyBossState

@export var duration: float = 15
@export var missile_interval_range: Vector2 = Vector2(2, 3)

@onready var left_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner"
@onready var right_missile_spawner: ProjectileSpawner = $"../../Spawners/ProjectileSpawner2"
@onready var pattern_projectile_spawner: PatternProjectileSpawner = $"../../Spawners/PatternProjectileSpawner"

var _timer: SceneTreeTimer

func enter() -> void:
	left_missile_spawner.spawn_interval = randf_range(
		missile_interval_range.x,
		missile_interval_range.y)
		
	right_missile_spawner.spawn_interval = randf_range(
		missile_interval_range.x,
		missile_interval_range.y)
	
	left_missile_spawner.active = true
	right_missile_spawner.active = true
	pattern_projectile_spawner.active = true
	
	_timer = get_tree().create_timer(duration)
	_timer.timeout.connect(_on_timer_timeout)



func _on_timer_timeout() -> void:
	left_missile_spawner.active = false
	right_missile_spawner.active = false
	pattern_projectile_spawner.active = false
	
	state_machine.change_state(states["Idle"])
