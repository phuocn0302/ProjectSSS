extends GodotBossState

@export var transit_time: float = 1
@export var next_state: GodotBossState

var _timer: SceneTreeTimer 

func enter() -> void:
	_timer = Utils.create_timer(transit_time)
	_timer.timeout.connect(transit)


func exit() -> void:
	_timer.timeout.disconnect(transit)


func transit() -> void:
	assert(next_state)
		
	state_machine.change_state(next_state)
