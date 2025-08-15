class_name BossPhaseManager
extends Node

@export var health_component: HealthComponent
@export var state_machine: StateMachine
@export var phase_datas: Array[BossPhaseData]

var current_phase_index: int = -1
var _boss_idle_state: EnemyIdleState
var _boss_entrance_state: EnemyEntranceState

func _ready() -> void:
	assert(health_component)
	assert(state_machine)
	assert(not phase_datas.is_empty())
	
	for i in phase_datas.size():
		for path in phase_datas[i].states:
			var node = get_node_or_null(path)
			assert(node is State, "Invalid State node in phase %d" % i)
	
	# Sort data asc by health percentage
	phase_datas.sort_custom(
		func(a, b): return a.health_percentage < b.health_percentage
	)
	
	for s in state_machine.get_children():
		if s is EnemyIdleState:
			_boss_idle_state = s
		elif s is EnemyEntranceState:
			_boss_entrance_state = s
	
	assert(_boss_idle_state)
	
	if _boss_entrance_state:
		await _boss_entrance_state.finished
	
	# dummy null param
	_update_phase(null)
	
	health_component.health_depleted.connect(_update_phase)

## Use dummy _t varg because health depleted signal has amount arg
func _update_phase(_t) -> void:
	print(owner.name, " health: " ,health_component.current_health)
	
	var hp_percent = (health_component.current_health / health_component.max_health) * 100.0
	
	for i in range(phase_datas.size()):
		if hp_percent <= phase_datas[i].health_percentage:
			if current_phase_index != i:
				current_phase_index = i
				_boss_idle_state.states = _get_states_node(i)
				
				var first_state_node = get_node_or_null(phase_datas[i].first_state)
				if first_state_node is State:
					state_machine.next_state_requested = first_state_node
				
				print("Phase changed to ", i, " with states:", _boss_idle_state.states)
			break


func _get_states_node(index: int) -> Array[State]:
	var states: Array[State] = []
	
	for p in phase_datas[index].states:
		var node = self.get_node(p)
		assert(node is State, "Phase %d: Path %s is not a valid State node." % [index, p])
		states.append(node)
	
	return states
