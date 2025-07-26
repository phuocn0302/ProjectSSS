class_name Projectile
extends PoolableEntity

const ENTITY_SHADER = preload("res://shaders/entity_shader.gdshader")

@export var stats: ProjectileStats:
	get: return _stats
	set(value):
		_stats = value.duplicate()
		apply_stats()

@export var on_hide_sfx: PackedScene

var direction: Vector2 = Vector2.UP
var max_speed: float 
var lifetime: float
var lifetime_timer: SceneTreeTimer
var _stats: ProjectileStats
## Flag to fix sfx spawn again after hit
var _sfx_spawned: bool = false

@onready var projectile_particles: ProjectileParticles = $ProjectileParticles
@onready var hitbox_component: HitboxComponent = $HitboxComponent
@onready var sprite: Sprite2D = get_node_or_null("Sprite2D")
@onready var anim_sprite: AnimatedSprite2D = get_node_or_null("AnimatedSprite2D")

func _ready() -> void:
	assert(stats)
	start_lifetime_timer()
	apply_stats()


func apply_stats():
	max_speed = stats.max_speed
	lifetime = stats.life_time
	
	if hitbox_component:
		hitbox_component.damage = stats.damage
	
	if projectile_particles:
		projectile_particles.modulate = stats.sfx_color
	
	var shader_material := ShaderMaterial.new()
	shader_material.shader = ENTITY_SHADER
	shader_material.set_shader_parameter("outline_enabled", true)
	shader_material.set_shader_parameter("outline_color", stats.sfx_color)
	
	if sprite:
		sprite.material = shader_material
	
	if anim_sprite:
		anim_sprite.material = shader_material
	


func active() -> void:
	super.active()
	
	if hitbox_component:
		hitbox_component.activate()
	
	_reset_timer()
	start_lifetime_timer()
	
	_sfx_spawned = false


func deactive() -> void:
	super.deactive()
	_reset_timer()
	
	if hitbox_component:
		hitbox_component.deactivate()
	if on_hide_sfx and not _sfx_spawned:
		_spawn_sfx(on_hide_sfx)



func _process(delta: float) -> void:
	move(delta)


func move(delta: float) -> void:
	self.global_rotation = direction.angle()
	self.global_position += direction.normalized() * max_speed * delta


func start_lifetime_timer() -> void:
	lifetime_timer = get_tree().create_timer(lifetime)
	lifetime_timer.timeout.connect(deactive)


func _reset_timer() -> void:
	if lifetime_timer:
		if lifetime_timer.is_connected("timeout", Callable(self, "deactive")):
			lifetime_timer.timeout.disconnect(Callable(self, "deactive"))
		lifetime_timer = null

func _on_hitbox_component_hit() -> void:
	self.deactive()


func _spawn_sfx(sfx: PackedScene) -> void:
	var _sfx = sfx.instantiate()
	_sfx.global_position = self.global_position
	get_tree().current_scene.add_child(_sfx)
	
	_sfx_spawned = true
