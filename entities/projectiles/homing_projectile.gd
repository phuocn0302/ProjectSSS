class_name HomingProjectile
extends Projectile

@export var target_group: String = "player"
@export var rotate_speed: float = 90
@export var chase_delay: float = 0.5

var _chasing: bool = false

func _ready() -> void:
	super._ready()
	assert(chase_delay < lifetime)
	
	get_tree().create_timer(chase_delay).timeout.connect(
		func(): _chasing = true
	)


func _get_direction_to_target() -> Vector2:
	var targets = get_tree().get_nodes_in_group(target_group)
	if targets.is_empty():
		return direction 
	
	var closest = _get_closest_target(targets)
	if closest == null:
		return direction
	
	return (closest.global_position - global_position).normalized()


func _get_closest_target(targets: Array) -> Entity:
	var closest: Entity = null
	var min_dist := INF
	
	for entity in targets:
		if not (entity is Entity):
			continue
		var dist = global_position.distance_squared_to(entity.global_position)
		if dist < min_dist:
			min_dist = dist
			closest = entity
	
	return closest


func chase(delta: float) -> void:
	var target_dir := _get_direction_to_target()
	var angle_to_target := direction.angle_to(target_dir)
	
	var max_angle := deg_to_rad(rotate_speed) * delta
	var clamped_angle = clamp(angle_to_target, -max_angle, max_angle)
	
	direction = direction.rotated(clamped_angle).normalized()
	
	global_position += direction * max_speed * delta
	global_rotation = direction.angle()


func move(delta: float) -> void:
	if not _chasing:
		super.move(delta)
	else:
		chase(delta)
