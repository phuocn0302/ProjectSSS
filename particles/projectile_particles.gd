class_name ProjectileParticles
extends GPUParticles2D

@export var projectile: Projectile

func _ready() -> void:
	assert(projectile)
	self.local_coords = true
	projectile.activated.connect(func(): self.emitting = true)
	projectile.deactivated.connect(func(): emitting = false)
