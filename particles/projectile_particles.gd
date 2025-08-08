class_name ProjectileParticles
extends CPUParticles2D

var projectile

func _ready() -> void:
	projectile = get_parent()
	self.local_coords = true
	projectile.activated.connect(func(): self.emitting = true)
	projectile.deactivated.connect(func(): emitting = false)
