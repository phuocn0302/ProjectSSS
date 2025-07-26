class_name StateMachine
extends Node

@export var actor: Entity
@export var starting_state: State
@export var states: Dictionary[String, State]

var next_state_requested: State
var current_state: State

func setup(_actor: Entity) -> void:
	for child in get_children():
		if child is State:
			states[child.name] = child
			child.setup(_actor, self)
	
	change_state(starting_state)


func change_state(new_state: State) -> void:
	if current_state:
		current_state.exit()
	
	current_state = new_state
	
	if next_state_requested:
		current_state = next_state_requested
		next_state_requested = null
	
	current_state.enter()


func process_physics(_delta: float) -> void:
	current_state.process_physics(_delta)


func process_frame(_delta: float) -> void:
	current_state.process_frame(_delta)


func process_input(event: InputEvent) -> void:
	current_state.process_input(event)
