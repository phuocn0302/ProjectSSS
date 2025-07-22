class_name Projectile
extends PoolableEntity

@export var direction: Vector2 = Vector2.UP
@export var max_speed: float = 300
@export var lifetime: float = 1

var lifetime_timer: SceneTreeTimer

func _ready() -> void:
	start_lifetime_timer()


func active() -> void:
	super.active()
	start_lifetime_timer()


func _process(delta: float) -> void:
	self.global_rotation = direction.angle()


func _physics_process(delta: float) -> void:
	self.global_position += direction.normalized() * max_speed * delta


func start_lifetime_timer():
	if lifetime_timer and lifetime_timer.is_connected("timeout", Callable(self, "deactive")):
		lifetime_timer.timeout.disconnect(Callable(self, "deactive"))
		
	lifetime_timer = get_tree().create_timer(lifetime)
	lifetime_timer.timeout.connect(deactive)


func _on_hitbox_component_hit() -> void:
	self.deactive()
