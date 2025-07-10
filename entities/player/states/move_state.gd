class_name PlayerMoveState
extends PlayerState

var velocity := Vector2.ZERO

func process_frame(_delta: float) -> void:
	if player.input_vector == Vector2.ZERO and velocity.length_squared() < 1.0:
		state_machine.change_state(states["Idle"])


func process_physics(_delta: float) -> void:
	var target = player.input_vector * player.max_move_speed
	velocity = velocity.move_toward(target, player.move_accel * _delta)
	
	player.translate(velocity * _delta)
