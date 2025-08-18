extends GodotBossState

@export var idle_time: float = 1

var _last_state: GodotBossState
var _second_last_state: GodotBossState

func enter() -> void:
	var timer = Utils.create_timer(idle_time)
	timer.timeout.connect(
		Callable(state_machine, "change_state")
		.bind(pick_random_state())
	)


func pick_random_state() -> GodotBossState:
	var _states: Array[GodotBossState]
	
	match boss.current_phase:
		boss.Phase.PHASE1:
			_states = boss.phase1_states
		boss.Phase.PHASE2:
			_states = boss.phase2_states
		boss.Phase.PHASE3:
			_states = boss.phase3_states
	
	if _states.size() > 1:
		if _last_state == _second_last_state:
			_states = _states.filter(func(s): return s != _last_state)
	
	var random_state: GodotBossState = _states.pick_random()
	
	_second_last_state = _last_state
	_last_state = random_state
	
	if not random_state:
		return self
	
	return random_state
