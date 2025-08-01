extends GodotBossState

@export var duration: float = 8

var _timer: SceneTreeTimer

func enter() -> void:
	boss.target_projectile_spawner.active = true
	boss.pattern_projectile_spawner.active = true
	_timer = get_tree().create_timer(duration)
	_timer.timeout.connect(change_state)


func exit() -> void:
	boss.target_projectile_spawner.active = false
	boss.pattern_projectile_spawner.active = false
	_timer.timeout.disconnect(change_state)


func change_state() -> void:
	state_machine.change_state(states["DoublePunchAttack"])
