class_name SpawnerProjectile
extends Projectile

var spawner: CircleProjectileSpawner 

func _ready() -> void:
	super._ready()
	
	_add_spawner()


func active() -> void:
	super.active()
	
	spawner.active = true


func deactive() -> void:
	super.deactive()
	
	spawner.active = false


func _add_spawner() -> void:
	if not projectile_data is SpawnerProjectileData:
		push_error("ProjectileData is not not SpawnerProjetileData")
		return
	
	spawner = CircleProjectileSpawner.new()
	spawner.type = projectile_data.spawn_type
	spawner.projectile_data = projectile_data.projectile_to_spawn
	spawner.number_of_projectile = projectile_data.number_to_spawn
	spawner.projectile_direction = projectile_data.spawn_direction
	spawner.spawn_interval = projectile_data.spawn_interval
	spawner.rotate_pattern = projectile_data.rotate_pattern
	spawner.rotate_speed = projectile_data.rotate_speed
	
	self.add_child(spawner)
