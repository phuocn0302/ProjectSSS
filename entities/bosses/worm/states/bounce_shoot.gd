extends WormBossState

@export var charge_time: float = 1
@export var duration: float = 1

@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var bounce_shoot_charge_particles: GPUParticles2D = $"../../BounceShootChargeParticles"
@onready var bounce_proj_spawner: TargetProjectileSpawner = $"../../Spawners/BounceProjSpawner"

var _is_charging: bool = false
var _timer: SceneTreeTimer

func enter() -> void:
	_is_charging = true
	idle_move_component.active = false
	bounce_shoot_charge_particles.emitting = true
	
	_timer = Utils.create_timer(charge_time)
	_timer.timeout.connect(_start_shooting)


func exit() -> void:
	if _timer.is_connected("timeout", _start_shooting):
		_timer.timeout.disconnect(_start_shooting)
		
	if _timer.is_connected("timeout", _on_duration_end):
		_timer.timeout.disconnect(_on_duration_end)
	
	bounce_proj_spawner.active = false
	idle_move_component.active = true
	bounce_shoot_charge_particles.emitting = false


func process_frame(_delta: float) -> void:
	if not _is_charging or not boss.player:
		return
	
	boss.global_rotation = boss.global_position.direction_to(
		boss.player.global_position
	).angle() - PI/2


func _start_shooting() -> void:
	_is_charging = false
	bounce_proj_spawner.active = true
	
	_timer = Utils.create_timer(duration)
	_timer.timeout.connect(_on_duration_end)
	
	var _tween = create_tween().tween_property(boss, "global_rotation", 0, duration)


func _on_duration_end() -> void:
	state_machine.change_state(states["Idle"])
