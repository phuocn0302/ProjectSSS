extends GodotBossState

const CIRCLE_EXPLOSION = preload("res://particles/circle_explosion.tscn")

@onready var sprite: AnimatedSprite2D = $"../../AnimatedSprite2D"
@onready var components: Node2D = $"../../Components"
@onready var eye_particles: GPUParticles2D = $"../../EyeParticles"

func enter() -> void:
	for p in get_tree().get_nodes_in_group("projectile"):
		if p is Projectile:
			p.deactive()
	
	
	state_machine.active = false
	
	for c in components.get_children():
		c.active = false
	
	boss.left_arm.self_destruct()
	await boss.right_arm.self_destruct()
	
	await Utils.create_timer(1).timeout
	
	
	var sfx = CIRCLE_EXPLOSION.instantiate()
	sfx.global_position = boss.global_position
	get_tree().current_scene.add_child(sfx)
	sprite.hide()
	
	await Utils.create_timer(1).timeout
	eye_particles.emitting = false
	
	await Utils.create_timer(1).timeout
	boss.queue_free()
