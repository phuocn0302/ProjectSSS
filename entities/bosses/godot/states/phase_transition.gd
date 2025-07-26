extends GodotBossState

@export var transit_time: float = 1
@export var next_state: GodotBossState

func enter() -> void:
	var timer = get_tree().create_timer(transit_time)
	timer.timeout.connect(transit)


func transit() -> void:
	assert(next_state)
		
	state_machine.change_state(next_state)
