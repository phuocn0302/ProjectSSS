class_name State
extends Node

var state_machine: StateMachine
var actor: Entity

func setup(_actor: Entity, _state_machine: StateMachine) -> void:
	self.state_machine = _state_machine
	self.actor = _actor


func enter() -> void:
	pass


func exit() -> void:
	pass


func process_frame(_delta: float) -> void:
	pass


func process_physics(_delta: float) -> void:
	pass


func process_input(_event: InputEvent) -> void:
	pass
