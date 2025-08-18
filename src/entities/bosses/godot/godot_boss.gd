class_name GodotBoss
extends Enemy

signal defeated

var player: Player

@onready var right_arm: GodotArm = $Arms/RightArm
@onready var left_arm: GodotArm = $Arms/LeftArm

@onready var circle_projectile_spawner: CircleProjectileSpawner = $Spawner/CircleProjectileSpawner
@onready var target_projectile_spawner: TargetProjectileSpawner = $Spawner/TargetProjectileSpawner
@onready var pattern_projectile_spawner: PatternProjectileSpawner = $Spawner/PatternProjectileSpawner

@onready var state_machine: StateMachine = $StateMachine

@onready var health_component: HealthComponent = $Components/HealthComponent
@onready var idle_move_component: IdleMoveComponent = $Components/IdleMoveComponent
@onready var ghost_trailing_component: GhostTrailingComponent = $Components/GhostTrailingComponent
@onready var eye_particles: GPUParticles2D = $EyeParticles

@onready var punch_attack: Node = $StateMachine/PunchAttack
@onready var double_punch_attack: Node = $StateMachine/DoublePunchAttack
@onready var shoot_at_player_1: Node = $StateMachine/ShootAtPlayer1
@onready var shoot_at_player_2: Node = $StateMachine/ShootAtPlayer2
@onready var die: Node = $StateMachine/Die
@onready var enemy_entrance_state: EnemyEntranceState = $StateMachine/EnemyEntranceState


func _ready() -> void:
	state_machine.setup(self)


func _exit_tree() -> void:
	defeated.emit()


func _process(delta: float) -> void:
	player = get_tree().get_first_node_in_group("player")
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)


func _on_health_component_health_reached_zero() -> void:
	state_machine.change_state(die)
