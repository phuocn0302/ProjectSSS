extends EnemyDieState

const CIRCLE_EXPLOSION = preload("res://src/particles/circle_explosion.tscn")

@onready var main: Sprite2D = $"../../Main"
@onready var gun: AnimatedSprite2D = $"../../Gun"
@onready var gpu_particles_2d: GPUParticles2D = $"../../GPUParticles2D"

@onready var boss: CapyBoss = $"../.."

func enter() -> void:
	super.enter()
	await Utils.create_timer(3).timeout
	
	var sfx = CIRCLE_EXPLOSION.instantiate()
	sfx.global_position = actor.global_position
	get_tree().current_scene.add_child(sfx)
	
	main.hide()
	gun.hide()
	
	await Utils.create_timer(1).timeout
	gpu_particles_2d.emitting = false
	
	await Utils.create_timer(1).timeout
	actor.queue_free()
