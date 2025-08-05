class_name CapyBossState
extends State

var boss: CapyBoss
var states: Dictionary[String, State]

func setup(_actor: Entity, _state_machine: StateMachine) -> void:
	super.setup(_actor, _state_machine)
	boss = self.actor as CapyBoss
	
	states = state_machine.states
