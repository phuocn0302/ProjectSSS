class_name HitFlashComponent
extends Component

const HIT_FLASH = preload("res://shaders/hit_flash.tres")

@export var health_component: HealthComponent
@export var flash_time: float = 0.1

var _original_mat: Material = null
var _flash_mat: ShaderMaterial

func _ready() -> void:
	super._ready()
	assert(health_component, "[HitFlashComponent] HealthComponent is required")
	
	_flash_mat = HIT_FLASH.duplicate()
	_original_mat = entity.material
	
	for c in entity.get_children():
		if c is Sprite2D or c is AnimatedSprite2D:
			c.use_parent_material = true
	
	_flash_mat.set_shader_parameter("flash_strength", 0)
	health_component.health_depleted.connect(flash)


# health_depleted signal has 1 arg
func flash(_t: float) -> void:
	entity.material = _flash_mat
	_flash_mat.set_shader_parameter("flash_strength", 1)
	
	await get_tree().create_timer(flash_time).timeout
	
	_flash_mat.set_shader_parameter("flash_strength", 0)
	entity.material = _original_mat
