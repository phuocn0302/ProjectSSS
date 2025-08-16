class_name CapyBoss
extends Enemy

signal defeated

var player: Player
var default_position: Vector2

@onready var gun: AnimatedSprite2D = $Gun

@onready var state_machine: StateMachine = $StateMachine
@onready var hurtbox_component: HurtboxComponent = $Components/HurtboxComponent
@onready var health_component: HealthComponent = $Components/HealthComponent
@onready var idle_move_component: IdleMoveComponent = $Components/IdleMoveComponent
@onready var ghost_trailing_component: GhostTrailingComponent = $Components/GhostTrailingComponent
@onready var screen_shake_component: ScreenShakeComponent = $Components/ScreenShakeComponent

@onready var missile_shoot: CapyBossState = $StateMachine/MissileShoot
@onready var bullet_hell_laser: CapyBossState = $StateMachine/BulletHellLaser
@onready var sweep_laser: CapyBossState = $StateMachine/SweepLaser
@onready var die: EnemyDieState = $StateMachine/Die
@onready var enemy_entrance_state: EnemyEntranceState = $StateMachine/EnemyEntranceState

func _ready() -> void:
	state_machine.setup(self)
	
	idle_move_component.active = false
	ghost_trailing_component.active = false
	
	await enemy_entrance_state.finished
	
	idle_move_component.active = true
	ghost_trailing_component.active = true
	
	default_position = self.global_position


func _exit_tree() -> void:
	print("Capy defeated")
	defeated.emit()


func _process(delta: float) -> void:
	player = get_tree().get_first_node_in_group("player")
	
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)


func recoil_gun(amount: float = 10) -> void:
	screen_shake_component.shake()
	
	var start_pos = gun.position
	var tween = create_tween()
	tween.set_trans(Tween.TRANS_SPRING)
	
	tween.tween_property(gun, "position", start_pos + Vector2.UP * amount, 0.1)
	tween.tween_property(gun, "position", start_pos, 1)


func _on_health_component_health_reached_zero() -> void:
	state_machine.change_state(die)
