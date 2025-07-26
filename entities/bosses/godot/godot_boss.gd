class_name GodotBoss
extends Enemy

enum Phase {
	PHASE1,
	PHASE2,
	PHASE3,
}

@export var phase1_states: Array[GodotBossState]
@export var phase2_states: Array[GodotBossState]
@export var phase3_states: Array[GodotBossState]

var player: Player
var current_phase: Phase = Phase.PHASE1

@onready var right_arm: GodotArm = $Arms/RightArm
@onready var left_arm: GodotArm = $Arms/LeftArm

@onready var circle_projectile_spawner: CircleProjectileSpawner = $Spawner/CircleProjectileSpawner
@onready var target_projectile_spawner: TargetProjectileSpawner = $Spawner/TargetProjectileSpawner
@onready var pattern_projectile_spawner: PatternProjectileSpawner = $Spawner/PatternProjectileSpawner

@onready var state_machine: StateMachine = $StateMachine

@onready var health_component: HealthComponent = $Components/HealthComponent
@onready var idle_move_component: IdleMoveComponent = $Components/IdleMoveComponent
@onready var ghost_trailing_component: GhostTrailingComponent = $Components/GhostTrailingComponent
@onready var eye_particles: GPUParticles2D = $AnimatedSprite2D/EyeParticles

@onready var punch_attack: Node = $StateMachine/PunchAttack
@onready var double_punch_attack: Node = $StateMachine/DoublePunchAttack
@onready var shoot_at_player_1: Node = $StateMachine/ShootAtPlayer1
@onready var shoot_at_player_2: Node = $StateMachine/ShootAtPlayer2
@onready var phase_transition: GodotBossState = $StateMachine/PhaseTransition


func _ready() -> void:
	state_machine.setup(self)


func _process(delta: float) -> void:
	player = get_tree().get_first_node_in_group("player")
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)


func _phase1_transtion() -> void:
	await get_tree().create_timer(0.3).timeout
	state_machine.change_state(shoot_at_player_1)


func _phase2_transition() -> void:
	current_phase = Phase.PHASE2
	
	phase_transition.transit_time = left_arm.active_time + right_arm.active_time + 2
	phase_transition.next_state = punch_attack
	
	state_machine.next_state_requested = phase_transition
	
	await left_arm.activate()
	await right_arm.activate()
	
	eye_particles.emitting = true
	ghost_trailing_component.active = true
	idle_move_component.active = true


func _phase3_transition() -> void:
	current_phase = Phase.PHASE3
	
	phase_transition.transit_time = 2
	phase_transition.next_state = shoot_at_player_2
	
	state_machine.next_state_requested = phase_transition


func _phase_management() -> void:
	var max_health = health_component.max_health
	var current_health = health_component.current_health
	
	match current_phase:
		Phase.PHASE1:
			if current_health < max_health * 9 / 10:
				_phase2_transition()
		Phase.PHASE2:
			if current_health < max_health * 5 / 10:
				_phase3_transition()
		Phase.PHASE3:
			pass


func _on_health_component_health_depleted(_amount: float) -> void:
	print(health_component.current_health)
	
	_phase_management()
