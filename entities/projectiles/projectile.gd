class_name Projectile
extends PoolableEntity

@export var direction: Vector2 = Vector2.UP
@export var max_speed: float = 300
@export var lifetime: float = 1

@export var on_hit_sfx: PackedScene
@export var on_hide_sfx: PackedScene

var lifetime_timer: SceneTreeTimer

func _ready() -> void:
	start_lifetime_timer()


func active() -> void:
	super.active()
	start_lifetime_timer()


func deactive() -> void:
	super.deactive()
	
	if on_hide_sfx:
		_spawn_sfx(on_hide_sfx)


func _process(delta: float) -> void:
	move(delta)


func move(delta: float) -> void:
	self.global_rotation = direction.angle()
	self.global_position += direction.normalized() * max_speed * delta


func start_lifetime_timer():
	if lifetime_timer and lifetime_timer.is_connected("timeout", Callable(self, "deactive")):
		lifetime_timer.timeout.disconnect(Callable(self, "deactive"))
		
	lifetime_timer = get_tree().create_timer(lifetime)
	lifetime_timer.timeout.connect(deactive)


func _on_hitbox_component_hit() -> void:
	self.deactive()
	
	if on_hit_sfx: 
		_spawn_sfx(on_hit_sfx)


func _spawn_sfx(sfx: PackedScene) -> void:
	var _sfx = sfx.instantiate()
	_sfx.global_position = self.global_position
	get_tree().current_scene.add_child(_sfx)
