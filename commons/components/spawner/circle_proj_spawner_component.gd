class_name CircleProjectileSpawner
extends ProjectileSpawner

@export var number_of_projectile: int = 10
@export var rotate_pattern: bool = false

@export_category("Rotation")
# In degree
@export_range(-360, 360) var rotate_speed: float = 20

func spawn() -> void:
	var angle := TAU / number_of_projectile
	
	for i in range(number_of_projectile):
		var _projectile := object_pool.get_instance() as Projectile
		_projectile.direction = self.projectile_direction.rotated(angle * i) 
		_projectile.global_position = self.global_position
	
	if rotate_pattern:
		projectile_direction = projectile_direction.rotated(rotate_speed)
