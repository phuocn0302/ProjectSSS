class_name AndroidInputHandler
extends Node

const FLICK_DISTANCE := 20
const FLICK_TIME := 0.2

var player: Player
var state_machine: StateMachine
var states: Dictionary[String, State]

var touch_last_pos: Vector2 = Vector2.ZERO
var touch_active: bool = false
var touch_start_pos: Vector2 = Vector2.ZERO
var touch_start_time: float = 0.0

@onready var projectile_spawner: ProjectileSpawner = $"../ProjectileSpawner"

func setup(_player: Player, _state_machine: StateMachine):
	self.player = _player
	self.state_machine = _state_machine
	states = state_machine.states
	projectile_spawner.spawn_interval = _player.shoot_interval
	projectile_spawner.active = true


func _input(event: InputEvent) -> void:
	if event is InputEventScreenTouch:
		if event.pressed:
			touch_active = true
			touch_last_pos = event.position
			touch_start_pos = event.position
			touch_start_time = Time.get_ticks_msec() / 1000.0
		else:
			if touch_active:
				var elapsed = (Time.get_ticks_msec() / 1000.0) - touch_start_time
				var total_delta = event.position - touch_start_pos
				var distance = total_delta.length()

				if elapsed <= FLICK_TIME and distance >= FLICK_DISTANCE and player.dash_state.dash_cooldown_timer.is_stopped():
					player.input_vector = total_delta.normalized()
					state_machine.change_state(states["Dash"])
					player.input_vector = Vector2.ZERO
				else:
					player.input_vector = Vector2.ZERO

			touch_active = false

	elif event is InputEventScreenDrag and touch_active:
		var drag_delta: Vector2 = event.position - touch_last_pos
		touch_last_pos = event.position
		var drag_length_sqr = drag_delta.length_squared()
		
		if drag_length_sqr > 0.2:
			player.input_vector = drag_delta
			if player.input_vector.length() > 1:
				player.input_vector = player.input_vector.normalized()
