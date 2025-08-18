class_name ProjectileData
extends Resource

const PROJECTILE_HIT_PARTICLES = preload("res://src/particles/projectile_hit_particles.tscn")
const PROJECTILE_PARTICLES = preload("res://src/particles/projectile_particles.tscn")

@export_group("Texture")
@export var texture: Texture2D
@export var sprite_frames: SpriteFrames
## Turn this on if texture is asymetric or collision shape is not circle
@export var texture_rotate_with_dir: bool = false

@export_group("Collision")
@export var collision_shape: Shape2D
@export var shape_offset: Vector2 = Vector2.ZERO
@export_flags_2d_physics var collision_mask: int = 1

@export_group("Stats")
@export var speed: float = 200
@export var damage: float = 1
@export var life_time: float = 5

@export_group("Effects")
@export var vfx_color: Color = Color.RED
@export var on_hit_vfx: PackedScene = PROJECTILE_HIT_PARTICLES
@export var use_particles: bool = true
@export var particles: PackedScene = PROJECTILE_PARTICLES
