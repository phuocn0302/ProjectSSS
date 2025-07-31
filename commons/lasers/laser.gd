class_name Laser
extends Line2D

const LASER_PARTICLE_MAT = preload("res://particles/materials/laser_particle_mat.tres")

@export_flags_2d_physics var collision_mask: int = 1
@export var direction: Vector2 = Vector2.DOWN
@export var length: int = 320
@export var max_width: int = 12
@export var emit_time: float = 1
@export var cast_time: float = 0.2
@export var decay_time: float = 0.2

var end_point: Vector2
var particles: GPUParticles2D
var hitbox_component: HitboxComponent
var hitbox_collision: CollisionShape2D

func _ready() -> void:
	_setup_particles()
	_setup_hitbox()
	_cast_laser()


func _process(delta: float) -> void:
	self.points[1] = end_point


func _setup_laser() -> void:
	self.begin_cap_mode = Line2D.LINE_CAP_ROUND
	self.end_cap_mode = Line2D.LINE_CAP_ROUND
	self.default_color.a = 1
	self.width = 1
	
	self.points = [Vector2.ZERO, Vector2.ZERO]
	end_point = Vector2.ZERO


func _cast_laser() -> void:
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
		
	_decay_laser()


func _decay_laser() -> void:
	var tween = self.create_tween()
	tween.set_trans(Tween.TRANS_SINE)
	tween.set_ease(Tween.EASE_OUT)
	tween.tween_property(self, "default_color:a", 0, decay_time)
	tween.finished.connect(func():
		await get_tree().create_timer(1).timeout
		_cast_laser()
	)


func _setup_particles() -> void:
	if particles: 
		particles.queue_free()
	
	particles = GPUParticles2D.new()
	particles.amount = length / 2
	particles.position = direction * (length / 2)
	particles.lifetime = emit_time + decay_time
	particles.randomness = 100
	particles.emitting = false
	particles.one_shot = true
	
	var particles_mat = LASER_PARTICLE_MAT.duplicate()
	particles_mat.emission_box_extents = Vector3(max_width/2 + 3, length/2, 1)
	
	particles.process_material = particles_mat
	
	self.add_child(particles)


func _setup_hitbox() -> void:
	hitbox_component = HitboxComponent.new()
	hitbox_collision = CollisionShape2D.new()
	
	hitbox_collision.shape = RectangleShape2D.new()
	hitbox_collision.shape.size = Vector2(max_width - 1, length - 1)
	hitbox_collision.position = direction * (length / 2)
	
	hitbox_component.active = false
	hitbox_component.collision_mask = collision_mask
	hitbox_component.add_child(hitbox_collision)
	
	self.add_child(hitbox_component)
	
