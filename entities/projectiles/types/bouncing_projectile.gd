class_name BouncingProjectile
extends Projectile

func _process(delta: float) -> void:
	super(delta)
	_check_boundary()


func _check_boundary() -> void:
	var size := self.projectile_data.collision_shape.get_rect().size
	
	var half_size = size * 0.5

	var hit_right = global_position.x + half_size.x > Utils.SCREEN_SIZE.size.x
	var hit_left = global_position.x - half_size.x < 0
	var hit_top = global_position.y - half_size.y < 0
	var hit_bottom = global_position.y + half_size.y > Utils.SCREEN_SIZE.size.y

	if hit_right or hit_left:
		direction.x *= -1
	if hit_top or hit_bottom:
		direction.y *= -1
