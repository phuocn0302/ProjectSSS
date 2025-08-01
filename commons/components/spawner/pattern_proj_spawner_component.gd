class_name PatternProjectileSpawner
extends ProjectileSpawner

@export var number_of_projectiles: int = 10
## Seconds to reach target shape
@export var travel_time: float = 0.5 

@onready var path2d: Path2D = $Path2D

var cached_directions: Array[Vector2] = []
var cached_distances: Array[float] = []

func _ready() -> void:
	super._ready()
	assert(path2d)
	_cache_pattern()


func spawn() -> void:
	if cached_directions.size() != number_of_projectiles:
		_cache_pattern()
	
	for i in number_of_projectiles:
		var instance = object_pool.get_instance() as Projectile
		instance.global_position = global_position
		instance.direction = cached_directions[i]
		instance.max_speed = cached_distances[i] / travel_time


func _cache_pattern() -> void:
	cached_directions.clear()
	cached_distances.clear()
	
	if number_of_projectiles <= 0 or path2d.curve.get_point_count() < 2:
		push_error("Invalid pattern or number of projectiles.")
		return
	
	for i in number_of_projectiles:
		var follow := PathFollow2D.new()
		path2d.add_child(follow)
		
		follow.progress_ratio = float(i) / max(1, number_of_projectiles - 1)
		var pattern_pos = follow.global_position
		var dir = (pattern_pos - global_position).normalized()
		var dist = pattern_pos.distance_to(global_position)
		
		cached_directions.append(dir)
		cached_distances.append(dist)
		
		follow.queue_free()
