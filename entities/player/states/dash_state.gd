class_name PlayerDashState
extends PlayerState

@onready var dash_timer: Timer = $DashTimer
@onready var dash_cooldown_timer: Timer = $DashCooldownTimer

var dash_direction: Vector2 = Vector2.ZERO
var can_dash: bool = false

func enter() -> void:
	super.enter()
	
	if player.input_vector == Vector2.ZERO:
		dash_direction = Vector2.UP
	else:
		dash_direction = player.input_vector 
	
	dash_timer.start(player.dash_time)


func process_physics(_delta: float) -> void:
	player.translate(player.dash_speed * dash_direction * _delta)


func exit() -> void:
	if dash_cooldown_timer.is_stopped():
		dash_cooldown_timer.start(player.dash_cooldown)


func _on_dash_timer_timeout() -> void:
	state_machine.change_state(states["Idle"])
