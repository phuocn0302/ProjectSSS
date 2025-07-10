class_name Player
extends Entity


const NORMAL_BULLET = preload("res://entities/player/projectiles/normal_bullet.tscn")

@export_category("Movement")
@export var max_move_speed: float = 150
@export var min_move_speed: float = 50
@export var move_accel: float = 2000

@export_category("Dash")
@export var dash_speed: float = 600
@export var dash_time: float = 0.1
@export var dash_cooldown: float = 0.4

@export_category("Shoot")
@export var shoot_interval: float = 0.2
@export var bullet_pool_size: int = 20

@onready var bullet_pool: ObjectPool = $BulletPool

@onready var input_handler: PlayerInputHandler = $InputHandler

@onready var state_machine: StateMachine = $StateMachine as StateMachine
@onready var idle_state: PlayerIdleState = $StateMachine/Idle
@onready var move_state: PlayerMoveState = $StateMachine/Move
@onready var dash_state: PlayerDashState = $StateMachine/Dash


var input_vector := Vector2.ZERO

func _ready() -> void:
	state_machine.setup(self)
	input_handler.setup(self, state_machine)
	bullet_pool.setup(NORMAL_BULLET, bullet_pool_size)


func _process(delta: float) -> void:
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)
