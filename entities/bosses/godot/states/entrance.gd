extends GodotBossState

signal finished

@export var speed: float = 20
@export var destination_point: Vector2 = Vector2(90,60)

var entered_phase_1: bool = false

@onready var hurtbox_component: HurtboxComponent = $"../../Components/HurtboxComponent"
@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var eye_particles: GPUParticles2D = $"../../EyeParticles"

func enter() -> void:
	hurtbox_component.active = false
	idle_move_component.active = false
	eye_particles.emitting = false


func exit() -> void:
	hurtbox_component.active = true


func process_frame(_delta: float) -> void:
	if not entered_phase_1 and boss.global_position.distance_squared_to(destination_point) < 0.5:
		entered_phase_1 = true
		finished.emit()


func process_physics(delta: float) -> void:
	boss.global_position = boss.global_position.move_toward(
		destination_point,
		speed * delta
	) 
