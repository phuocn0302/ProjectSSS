extends Node

func create_timer(duration: float, process_always: bool = false, process_in_physics: bool = false, ignore_time_scale: bool = false) -> SceneTreeTimer:
	return get_tree().create_timer(duration, process_always, process_in_physics, ignore_time_scale)
