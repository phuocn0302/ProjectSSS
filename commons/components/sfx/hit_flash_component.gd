class_name HitFlashComponent
extends Component

@export var sprite: Node2D
@export var health_component: HealthComponent
@export var flash_time: float = 0.1


func _ready() -> void:
	super._ready()
	
	assert(sprite or (sprite is Sprite2D or sprite is AnimatedSprite2D))
	
	if health_component:
		health_component.health_depleted.connect(flash)


func flash_no_args() -> void:
	flash(null)


func flash(_args: Variant) -> void:
	var _mat = sprite.material
	_mat.set_shader_parameter("flash_strength", 1)
	
	await get_tree().create_timer(flash_time).timeout
	
	_mat.set_shader_parameter("flash_strength", 0)
