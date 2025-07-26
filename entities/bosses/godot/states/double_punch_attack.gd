extends GodotBossState

@export var follow_speed: float = 120
@export var player_offset: float = 80
@export var attack_delay: float = 3
@export var punch_prepare_time: float = 0.4
@export var punch_time: float = 0.2
@export var arm_return_time: float = 1
@export var punch_distance: float = 150
@export var wind_up_offset: float = 30

# Alternate right arm offset side (left/right of player)
var _right_arm_attack_pos_index = 0
var _timer: SceneTreeTimer

@onready var right_arm_pos: Marker2D = $"../../Arms/RightArmPos"
@onready var left_arm_pos: Marker2D = $"../../Arms/LeftArmPos"

func enter() -> void:
	boss.circle_projectile_spawner.active = true
	
	_right_arm_attack_pos_index = fposmod(_right_arm_attack_pos_index + 1, 2)
	
	_timer = get_tree().create_timer(attack_delay)
	_timer.timeout.connect(_attack)


func process_frame(delta: float) -> void:
	if _timer.time_left > 0:
		_follow_player(delta)


func exit() -> void:
	boss.circle_projectile_spawner.active = false
	boss.right_arm.rotation = 0
	boss.right_arm.scale = Vector2.ONE


func _follow_player(delta: float) -> void:
	if not boss.player: 
		return
	var player := boss.player as Player
	var left_arm = boss.left_arm
	var right_arm = boss.right_arm
	
	var left_arm_offset = Vector2(
			player.global_position.x,
			player.global_position.y - player_offset 
		)
	
	var right_arm_offset = [
		Vector2(
			player.global_position.x + player_offset,
			player.global_position.y 
		),
		Vector2(
			player.global_position.x - player_offset,
			player.global_position.y, 
		),
	][_right_arm_attack_pos_index]
	
	left_arm.global_position = left_arm.global_position.move_toward(
		left_arm_offset,
		follow_speed * delta
		)
	
	right_arm.global_position = right_arm.global_position.move_toward(
		right_arm_offset,
		follow_speed * delta
		)

func _rotate_right_arm() -> void:
	var tween = create_tween().set_parallel()
	if _right_arm_attack_pos_index == 0:
		boss.right_arm.scale = Vector2(1,-1)
	tween.tween_property(boss.right_arm, "rotation", -PI/2, punch_prepare_time)


func _attack() -> void:
	_left_arm_attack()
	_rotate_right_arm()
	_right_arm_attack()


func _left_arm_attack() -> void:
	var left_arm := boss.left_arm
	var start_pos := left_arm.position
	var prepare_offset := Vector2.UP * 30
	var punch_offset := Vector2.DOWN * punch_distance
	
	var prepare_pos := start_pos + prepare_offset
	var punch_pos := prepare_pos + punch_offset
	
	left_arm.ghost_trailing_component.active = true
	var tween = create_tween()
	
	tween.set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	tween.tween_property(left_arm, "position", prepare_pos, punch_prepare_time)
	
	tween.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_IN)
	tween.tween_property(left_arm, "position", punch_pos, punch_time)
	
	tween.tween_callback(Callable(left_arm, "explode"))
	
	tween.tween_property(left_arm, "global_position", left_arm_pos.global_position, arm_return_time)
	
	await tween.finished
	left_arm.ghost_trailing_component.active = false


func _right_arm_attack() -> void:
	var right_arm := boss.right_arm
	var start_pos := right_arm.position
	var prepare_offset := Vector2.RIGHT * wind_up_offset
	var punch_offset := Vector2.LEFT * punch_distance
	
	# Right arm is in right side of player, counter already +1 from _ready
	if _right_arm_attack_pos_index == 1:
		prepare_offset = Vector2.LEFT * wind_up_offset
		punch_offset = Vector2.RIGHT * punch_distance
	
	var prepare_pos := start_pos + prepare_offset
	var punch_pos := prepare_pos + punch_offset
	
	right_arm.ghost_trailing_component.active = true
	
	var tween = create_tween()
	
	tween.set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	tween.tween_property(right_arm, "position", prepare_pos, punch_prepare_time)

	tween.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_IN)
	tween.tween_property(right_arm, "position", punch_pos, punch_time)
	
	tween.tween_callback(Callable(right_arm, "explode"))
	
	tween.tween_property(right_arm, "global_position", right_arm_pos.global_position, arm_return_time)
	
	await tween.finished
	right_arm.ghost_trailing_component.active = false
	
	state_machine.change_state(states["ShootAtPlayer2"])
