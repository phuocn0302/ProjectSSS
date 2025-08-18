class_name GodotBossState
extends State

var boss: GodotBoss
var states: Dictionary[String, State]

func setup(_actor: Entity, _state_machine: StateMachine) -> void:
	super.setup(_actor, _state_machine)
	boss = self.actor as GodotBoss
	
	states = state_machine.states
