class_name PlayerInputHandler
extends Node

@onready var shoot_cooldown: Timer = $ShootCooldown

var player: Player
var state_machine: StateMachine
var states: Dictionary[String, State]

var is_shooting: bool = false
var can_shoot: bool = true

func setup(_player: Player, _state_machine: StateMachine):
	self.player = _player
	self.state_machine = _state_machine
	states = state_machine.states
	shoot_cooldown.wait_time = player.shoot_interval


func _input(event: InputEvent) -> void:
	player.input_vector = Input.get_vector("move_left", "move_right", "move_up", "move_down")
	
	if event.is_action_pressed("dash") and player.dash_state.dash_cooldown_timer.is_stopped():
		state_machine.change_state(states["Dash"])
	
	if event.is_action_pressed("shoot") and can_shoot:
		is_shooting = true
		can_shoot = false
		shoot()
	
	if event.is_action_released("shoot"):
		is_shooting = false
	
	state_machine.process_input(event)


func shoot() -> void:
	var bullet = player.bullet_pool.get_instance()
	bullet.global_position = player.global_position
	bullet.direction = Vector2.UP
	
	shoot_cooldown.start()


func _on_shoot_cooldown_timeout() -> void:
	can_shoot = true
	if is_shooting:
		shoot()
