class_name Laser
extends Line2D

const LASER_PARTICLE_MAT = preload("res://particles/materials/laser_particle_mat.tres")
const ENTITY_MAT = preload("res://shaders/entity_mat.tres")

@export var laser_stats: LaserStats: set = _apply_stats

var collision_mask: int
var direction: Vector2
var length: int
var max_width: int
var emit_time: float
var cast_time: float
var decay_time: float

var end_point: Vector2
var particles: GPUParticles2D
var hitbox_component: HitboxComponent
var hitbox_collision: CollisionShape2D

func _ready() -> void:
	assert(laser_stats)
	_setup_laser()


@warning_ignore("unused_parameter")
func _process(delta: float) -> void:
	self.points[1] = end_point


func cast() -> void:
	_setup_laser()
	
	if particles: 
		particles.emitting = true
	
	var tween = self.create_tween().set_parallel(true)
	tween.set_trans(Tween.TRANS_EXPO)
	tween.set_ease(Tween.EASE_IN_OUT)
	tween.tween_property(self, "end_point", direction * length, cast_time/ 2)
	tween.tween_property(self, "width", max_width, cast_time)
	
	await tween.finished
	
	if hitbox_component:
		hitbox_component.active = true
		
	await get_tree().create_timer(emit_time).timeout
	
	if hitbox_component:
		hitbox_component.active = false
		
	_decay()


func _decay() -> void:
	var tween = self.create_tween()
	tween.set_trans(Tween.TRANS_SINE)
	tween.set_ease(Tween.EASE_OUT)
	tween.tween_property(self, "default_color:a", 0, decay_time)


func _apply_stats(stats: LaserStats) -> void:
	laser_stats = stats
	self.collision_mask = stats.collision_mask
	self.direction = stats.direction
	self.length = stats.length
	self.max_width = stats.max_width
	self.emit_time = stats.emit_time
	self.cast_time = stats.cast_time
	self.decay_time = stats.decay_time
	
	_setup_particles()
	_setup_hitbox()


func _setup_laser() -> void:
	self.begin_cap_mode = Line2D.LINE_CAP_ROUND
	self.end_cap_mode = Line2D.LINE_CAP_ROUND
	self.default_color.a = 1
	self.width = 1
	
	self.points = [Vector2.ZERO, Vector2.ZERO]
	end_point = Vector2.ZERO


func _setup_particles() -> void:
	if not particles: 
		particles = GPUParticles2D.new()
		self.add_child(particles)
	
	@warning_ignore("integer_division")
	particles.amount = length / 2
	@warning_ignore("integer_division")
	particles.position = direction * (length / 2)
	particles.lifetime = emit_time + decay_time
	particles.randomness = 100
	particles.show_behind_parent = true
	particles.emitting = false
	particles.one_shot = true
	
	var particles_mat = LASER_PARTICLE_MAT.duplicate()
	@warning_ignore("integer_division")
	particles_mat.emission_box_extents = Vector3(max_width/2 + 3, length/2, 1)
	
	particles.process_material = particles_mat


func _setup_hitbox() -> void:
	if not hitbox_component:
		hitbox_component = HitboxComponent.new()
		self.add_child(hitbox_component)
	
	if not hitbox_collision:
		hitbox_collision = CollisionShape2D.new()
		hitbox_collision.shape = RectangleShape2D.new()
		hitbox_component.add_child(hitbox_collision)
	
	hitbox_collision.shape.size = Vector2(max_width - 1, length - 1)
	@warning_ignore("integer_division")
	hitbox_collision.position = direction * (length / 2)
	
	hitbox_component.active = false
	hitbox_component.collision_mask = collision_mask
