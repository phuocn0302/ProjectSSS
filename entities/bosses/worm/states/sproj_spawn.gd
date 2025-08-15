extends WormBossState

@export var duration: float = 6
@onready var target_projectile_spawner: TargetProjectileSpawner = $"../../Spawners/TargetProjectileSpawner"

var _timer: SceneTreeTimer

func enter() -> void:
	target_projectile_spawner.active = true
	
	_timer = Utils.create_timer(duration)
	_timer.timeout.connect(_on_timer_timeout)


func exit() -> void:
	_timer.timeout.disconnect(_on_timer_timeout)
	target_projectile_spawner.active = false


func _on_timer_timeout() -> void:
	state_machine.change_state(states["Idle"])
