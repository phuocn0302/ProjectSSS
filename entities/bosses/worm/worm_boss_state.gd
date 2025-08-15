class_name WormBossState
extends State

var boss: WormBoss
var states: Dictionary[String, State]

func setup(_actor: Entity, _state_machine: StateMachine) -> void:
	super.setup(_actor, _state_machine)
	boss = self.actor as WormBoss
	
	states = state_machine.states
