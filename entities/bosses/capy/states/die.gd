extends CapyBossState

const CIRCLE_EXPLOSION = preload("res://particles/circle_explosion.tscn")

@onready var main: Sprite2D = $"../../Main"
@onready var gun: AnimatedSprite2D = $"../../Gun"
@onready var components: Node2D = $"../../Components"
@onready var spawners: Node2D = $"../../Spawners"
@onready var gpu_particles_2d: GPUParticles2D = $"../../GPUParticles2D"


func enter() -> void:
	for p in get_tree().get_nodes_in_group("projectile"):
		if p is Projectile:
			p.deactive()
	
	for c in components.get_children():
		c.active = false
	
	for s in spawners.get_children():
		s.active = false
	
	state_machine.active = false
	await Utils.create_timer(3).timeout
	
	var sfx = CIRCLE_EXPLOSION.instantiate()
	sfx.global_position = boss.global_position
	get_tree().current_scene.add_child(sfx)
	
	main.hide()
	gun.hide()
	
	await Utils.create_timer(1).timeout
	gpu_particles_2d.emitting = false
	
	await Utils.create_timer(1).timeout
	boss.queue_free()
