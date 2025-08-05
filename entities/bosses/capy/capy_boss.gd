class_name CapyBoss
extends Enemy

signal defeated

enum Phase {
	PHASE1,
	PHASE2,
	PHASE3,
}

@export var phase1_states: Array[CapyBossState]
@export var phase2_states: Array[CapyBossState]
@export var phase3_states: Array[CapyBossState]

var player: Player
var current_phase: Phase = Phase.PHASE1
var default_position: Vector2

@onready var gun: AnimatedSprite2D = $Gun

@onready var state_machine: StateMachine = $StateMachine
@onready var hurtbox_component: HurtboxComponent = $Components/HurtboxComponent
@onready var health_component: HealthComponent = $Components/HealthComponent
@onready var idle_move_component: IdleMoveComponent = $Components/IdleMoveComponent
@onready var ghost_trailing_component: GhostTrailingComponent = $Components/GhostTrailingComponent

@onready var boss_entrance_state: BossEntranceState = $StateMachine/BossEntranceState
@onready var missile_shoot: CapyBossState = $StateMachine/MissileShoot
@onready var bullet_hell_laser: CapyBossState = $StateMachine/BulletHellLaser
@onready var sweep_laser: CapyBossState = $StateMachine/SweepLaser
@onready var die: CapyBossState = $StateMachine/Die


func _ready() -> void:
	state_machine.setup(self)


func _exit_tree() -> void:
	print("Capy defeated")
	defeated.emit()


func _process(delta: float) -> void:
	player = get_tree().get_first_node_in_group("player")
	
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)


func _phase1_transtion() -> void:
	default_position = self.global_position
	await get_tree().create_timer(0.3).timeout
	
	idle_move_component.active = true
	ghost_trailing_component.active = true
	
	state_machine.change_state(missile_shoot)


func _phase2_transition() -> void:
	current_phase = Phase.PHASE2
	state_machine.next_state_requested = bullet_hell_laser


func _phase3_transition() -> void:
	current_phase = Phase.PHASE3
	state_machine.next_state_requested = sweep_laser


func _phase_management() -> void:
	var max_health = health_component.max_health
	var current_health = health_component.current_health
	
	match current_phase:
		Phase.PHASE1:
			if current_health < max_health * 8 / 10:
				_phase2_transition()
		Phase.PHASE2:
			if current_health < max_health * 9 / 20:
				_phase3_transition()
		Phase.PHASE3:
			pass


func recoil_gun(amount: float = 10) -> void:
	var start_pos = gun.position
	var tween = create_tween()
	tween.set_trans(Tween.TRANS_SPRING)
	
	tween.tween_property(gun, "position", start_pos + Vector2.UP * amount, 0.1)
	tween.tween_property(gun, "position", start_pos, 1)


func _on_health_component_health_depleted(_amount: float) -> void:
	print(health_component.current_health)
	
	_phase_management()


func _on_health_component_health_reached_zero() -> void:
	state_machine.next_state_requested = null
	state_machine.change_state(die)
