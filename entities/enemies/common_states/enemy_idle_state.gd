class_name EnemyIdleState
extends State

@export var idle_time: float = 1
@export var states: Array[State]: set = _set_states

var _last_state: State
var _second_last_state: State
var _remaining_states: Array[State] = [] 

func enter() -> void:
	var timer = Utils.create_timer(idle_time)
	timer.timeout.connect(
		Callable(state_machine, "change_state")
		.bind(pick_random_state())
	)


func _set_states(val: Array[State]) -> void:
	states = val
	_remaining_states.clear()


func pick_random_state() -> State:
	if states.is_empty():
		return self
	
	if _remaining_states.is_empty():
		_remaining_states = states.duplicate()
		
	var candidates = _remaining_states.filter(func(s):
		return not (_last_state == s and _second_last_state == s)
	)
	
	if candidates.is_empty():
		candidates = _remaining_states.filter(func(s): return s != _last_state) \
			if _remaining_states.any(func(s): return s != _last_state) \
			else _remaining_states
	
	var random_state: State = candidates.pick_random()
	_remaining_states.erase(random_state)
	
	_second_last_state = _last_state
	_last_state = random_state
	
	print("[Idle] Next random state: ", random_state)
	return random_state
