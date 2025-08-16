extends GodotBossState

@export var next_state: GodotBossState

@onready var right_arm: GodotArm = $"../../Arms/RightArm"
@onready var left_arm: GodotArm = $"../../Arms/LeftArm"
@onready var eye_particles: GPUParticles2D = $"../../EyeParticles"
@onready var ghost_trailing_component: GhostTrailingComponent = $"../../Components/GhostTrailingComponent"
@onready var idle_move_component: IdleMoveComponent = $"../../Components/IdleMoveComponent"
@onready var screen_shake_component: ScreenShakeComponent = $"../../Components/ScreenShakeComponent"

func enter() -> void:
	screen_shake_component.shake()
	await left_arm.activate()
	screen_shake_component.shake()
	await right_arm.activate()
	
	eye_particles.emitting = true
	ghost_trailing_component.active = true
	idle_move_component.active = true
	
	state_machine.change_state(next_state)
