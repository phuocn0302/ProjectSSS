class_name PlayerState
extends State

var player: Player
var states: Dictionary[String, State]

func setup(_actor: Entity, _state_machine: StateMachine) -> void:
	super.setup(_actor, _state_machine)
	player = self.actor as Player
	
	states = _state_machine.states
