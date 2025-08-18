class_name SpawnerProjectileData
extends ProjectileData

@export var spawn_type: ProjectileFactory.Type = ProjectileFactory.Type.NORMAL
@export var projectile_to_spawn: ProjectileData
@export var spawn_direction: Vector2 = Vector2.DOWN
@export var number_to_spawn: float = 3
@export var spawn_interval: float = 0.5
@export var rotate_pattern: bool = false
@export var rotate_speed: float = 20
