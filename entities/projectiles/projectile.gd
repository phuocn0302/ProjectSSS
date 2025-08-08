class_name Projectile
extends PoolableEntity

const ENTITY_SHADER = preload("res://shaders/entity_shader.gdshader")

@export var projectile_data: ProjectileData: set = _set_projectile_data

var direction: Vector2 = Vector2.UP
## Flag to fix sfx spawn again after hit
var _vfx_spawned: bool = false
var _is_visible: bool = true

var _collision_query := PhysicsShapeQueryParameters2D.new()

var sprite_node: Node
var particles_node: CPUParticles2D
var lifetime_timer: SceneTreeTimer

func _ready() -> void:
	add_to_group("projectile")
	assert(projectile_data)
	
	_start_lifetime_timer()


func _process(delta: float) -> void:
	move(delta)
	_check_visible()
	_update_hitbox() 


func active() -> void:
	super.active()
	
	_reset_timer()
	_start_lifetime_timer()
	
	_collision_query.collide_with_areas = true
	
	_vfx_spawned = false


func deactive() -> void:
	super.deactive()
	_reset_timer()
	
	_collision_query.collide_with_areas = false
	
	if not _is_visible:
		return
	
	if projectile_data.on_hit_vfx and not _vfx_spawned:
		_spawn_sfx(projectile_data.on_hit_vfx)


func move(delta: float) -> void:
	self.global_rotation = direction.angle()
	self.global_position += direction.normalized() * projectile_data.speed * delta


func _set_projectile_data(data: ProjectileData) -> void:
	projectile_data = data
	
	_set_sprite()
	
	if data.use_particles:
		_set_particles()
	
	_set_hitbox()


func _set_sprite() -> void:
	var use_animated := projectile_data.sprite_frames != null
	
	if not is_instance_valid(sprite_node):
		sprite_node = get_node_or_null("ProjectileSprite2D")

	if is_instance_valid(sprite_node):
		if (use_animated and not sprite_node is AnimatedSprite2D) or (not use_animated and not sprite_node is Sprite2D):
			remove_child(sprite_node)
			sprite_node.queue_free()
			sprite_node = null
			
	if not is_instance_valid(sprite_node):
		if use_animated:
			sprite_node = AnimatedSprite2D.new()
		else:
			sprite_node = Sprite2D.new()
		sprite_node.name = "ProjectileSprite2D"
		add_child(sprite_node)
		
	if use_animated:
		var animated_sprite = sprite_node as AnimatedSprite2D
		animated_sprite.sprite_frames = projectile_data.sprite_frames
		animated_sprite.play("default")
	else:
		var static_sprite = sprite_node as Sprite2D
		static_sprite.texture = projectile_data.texture

	_set_shader(sprite_node as CanvasItem)


func _set_particles() -> void:
	if not projectile_data.particles:
		return
	
	if not is_instance_valid(particles_node):
		particles_node = get_node_or_null("Particles")
		
		if not particles_node:
			particles_node = projectile_data.particles.instantiate() as ProjectileParticles
			particles_node.name = "Particles"
			particles_node.projectile = self
			add_child(particles_node)
	
	particles_node.modulate = projectile_data.vfx_color


func  _set_hitbox() -> void:
	_collision_query.shape = projectile_data.collision_shape
	_collision_query.collide_with_bodies = false
	_collision_query.collide_with_areas = true
	_collision_query.collision_mask = projectile_data.collision_mask
	
	var offset_transform := Transform2D.IDENTITY
	offset_transform.origin = projectile_data.shape_offset
	_collision_query.transform = global_transform * offset_transform


func _set_shader(_sprite: CanvasItem) -> void:
	var shader_material := ShaderMaterial.new()
	shader_material.shader = ENTITY_SHADER
	shader_material.set_shader_parameter("outline_enabled", true)
	shader_material.set_shader_parameter("outline_color", projectile_data.vfx_color)
	
	_sprite.material = shader_material


func _update_hitbox() -> void:
	var offset_transform := Transform2D.IDENTITY
	offset_transform.origin = projectile_data.shape_offset
	_collision_query.transform = global_transform * offset_transform
	
	var hits = get_world_2d().direct_space_state.intersect_shape(_collision_query)
	
	if hits.size() > 0:
		_on_collision(hits)


func _check_visible() -> void:
	_is_visible = Utils.SCREEN_SIZE.grow(10).has_point(global_position)
	visible = _is_visible
	
	var expanded_rect := Utils.SCREEN_SIZE.grow(50)

	if not expanded_rect.has_point(global_position):
		deactive()


func _on_collision(hits: Array) -> void:
	for hit in hits:
		var collider = hit["collider"]
		if collider is HurtboxComponent:
			
			if not collider.active:
				return
			
			collider.take_damage(projectile_data.damage)
			deactive()


func _start_lifetime_timer() -> void:
	lifetime_timer = Utils.create_timer(projectile_data.life_time, false)
	lifetime_timer.timeout.connect(deactive)


func _reset_timer() -> void:
	if lifetime_timer:
		if lifetime_timer.is_connected("timeout", Callable(self, "deactive")):
			lifetime_timer.timeout.disconnect(Callable(self, "deactive"))
		lifetime_timer = null


func _spawn_sfx(vfx: PackedScene) -> void:
	var _vfx = vfx.instantiate()
	_vfx.global_position = self.global_position
	get_tree().current_scene.add_child(_vfx)
	
	_vfx_spawned = true
