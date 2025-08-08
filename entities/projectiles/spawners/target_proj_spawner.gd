class_name TargetProjectileSpawner
extends ProjectileSpawner

@export var target_group: String = "player"

func _set_direction() -> void:
	var targets = get_tree().get_nodes_in_group(target_group)
	if targets.is_empty():
		return 
	
	var closest = _get_closest_target(targets)
	if closest == null:
		return 
	
	projectile_direction = (closest.global_position - global_position).normalized()


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


func spawn() -> void:
	_set_direction()
	super.spawn()
