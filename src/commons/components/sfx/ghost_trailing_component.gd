class_name GhostTrailingComponent
extends Component

@export var ghost_spawn_interval: float = 0.1
@export var ghost_decay_time: float = 0.3
@export var sprite: Sprite2D
@export var anim_sprite: AnimatedSprite2D
@export_range(0.0, 1.0) var ghost_transparency: float = 0.5

var object_pool: ObjectPool
var _spawn_timer := 0.0

func _ready() -> void:
	super._ready()
	assert(sprite or anim_sprite)
	object_pool = ObjectPool.new()
	add_child(object_pool)
	
	var ghost_scene := preload_ghost_scene()
	object_pool.setup(ghost_scene)


func _physics_process(delta: float) -> void:
	_spawn_timer += delta
	if _spawn_timer >= ghost_spawn_interval:
		spawn_ghost()
		_spawn_timer = 0.0


func activate() -> void:
	_spawn_timer = 0
	super.activate()


func deactive() -> void:
	_spawn_timer = 0
	super.deactivate()


func spawn_ghost() -> void:
	var ghost := object_pool.get_instance() as PoolableEntity
	if ghost == null:
		return
	
	var ghost_sprite := ghost.get_node("Ghost") as Sprite2D
	
	if sprite:
		ghost_sprite.texture = sprite.texture
		ghost.global_position = sprite.global_position
		ghost.global_rotation = sprite.global_rotation
		ghost.scale = sprite.scale
	elif anim_sprite:
		ghost_sprite.texture = anim_sprite.sprite_frames.get_frame_texture(anim_sprite.animation, anim_sprite.frame)
		ghost.global_position = anim_sprite.global_position
		ghost.global_rotation = anim_sprite.global_rotation
		ghost.scale = anim_sprite.scale
	
	ghost_sprite.modulate = Color(1, 1, 1, ghost_transparency)
	
	ghost.create_tween()\
		.tween_property(ghost_sprite, "modulate:a", 0.0, ghost_decay_time)\
		.set_trans(Tween.TRANS_LINEAR)\
		.set_ease(Tween.EASE_OUT)\
		.connect("finished", func(): ghost.deactive())


func preload_ghost_scene() -> PackedScene:
	var ghost_node := PoolableEntity.new()
	var _sprite := Sprite2D.new()
	_sprite.name = "Ghost"
	_sprite.z_index = -1
	_sprite.visible = true
	
	ghost_node.add_child(_sprite)
	_sprite.owner = ghost_node 
	
	var ghost_scene := PackedScene.new()
	ghost_scene.pack(ghost_node)
	return ghost_scene
