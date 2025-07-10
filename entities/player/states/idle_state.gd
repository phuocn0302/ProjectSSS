class_name PlayerIdleState
extends PlayerState

func process_frame(_delta: float) -> void:
	if player.input_vector != Vector2.ZERO:
		state_machine.change_state(states["Move"])
