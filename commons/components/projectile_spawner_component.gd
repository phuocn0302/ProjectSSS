class_name ProjectileSpawner
extends Component2D

@export var projectile: PackedScene
@export var projectile_direction: Vector2 = Vector2.UP
@export var spawn_interval: float = 0.5:
	get: return spawn_interval
	set(value):
		spawn_interval = value
		if timer:
			timer.wait_time = value
@export var pool_size: int = 20

var object_pool: ObjectPool
var timer: Timer

func _ready() -> void:
	super._ready()
	assert(projectile)
	
	setup_pool()
	setup_timer()


func activate() -> void:
	if timer:
		timer.start()
	super.activate()


func deactivate() -> void:
	if timer:
		timer.stop()
	super.deactivate()


func setup_pool() -> void:
	if object_pool:
		object_pool.queue_free()
		
	object_pool = ObjectPool.new()
	add_child(object_pool)
	
	object_pool.setup(projectile, pool_size)


func setup_timer() -> void:
	if timer:
		timer.queue_free()
	
	timer = Timer.new()
	add_child(timer)
	
	timer.timeout.connect(spawn)
	timer.wait_time = spawn_interval
	timer.autostart = true
	timer.start()


func spawn() -> void:
	var _projectile := object_pool.get_instance() as Projectile
	_projectile.direction = projectile_direction
	_projectile.global_position = self.global_position
