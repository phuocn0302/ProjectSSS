class_name ProjectileFactory
extends Node

enum Type {
	NORMAL,
	HOMING,
	SPAWNER,
	BOUCING,
}

static func create_projectile(data: ProjectileData, type: Type) -> Projectile:
	var proj: Projectile
	
	match type:
		Type.NORMAL:
			proj = Projectile.new()
		
		Type.HOMING:
			proj = HomingProjectile.new()
		
		Type.SPAWNER:
			proj = SpawnerProjectile.new()
			
		Type.BOUCING:
			proj = BouncingProjectile.new()
		
	proj.projectile_data = data
	
	return proj
