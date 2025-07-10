class_name PlayerInputHandler
extends Node

var player: Player
var state_machine: StateMachine
var states: Dictionary[String, State]

func setup(_player: Player, _state_machine: StateMachine):
	self.player = _player
	self.state_machine = _state_machine
	states = state_machine.states


func _input(event: InputEvent) -> void:
	player.input_vector = Input.get_vector("move_left", "move_right", "move_up", "move_down")
	
	if event.is_action_pressed("dash") and player.dash_state.dash_cooldown_timer.is_stopped():
		state_machine.change_state(states["Dash"])
	
	
	state_machine.process_input(event)
