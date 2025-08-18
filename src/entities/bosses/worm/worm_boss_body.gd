class_name WormBossBody
extends Entity

const CIRCLE_EXPLOSION = preload("res://src/particles/circle_explosion.tscn")

@export var worm_boss: WormBoss
@export var move_speed: float = 15
## For rotating sprite
@export var next_segment: Entity
@export var next_segment_joint: Marker2D

@onready var joint_segment_pos: Marker2D = $JointSegmentPos
@onready var sprite: AnimatedSprite2D = $AnimatedSprite2D
@onready var hit_flash_component: HitFlashComponent = $Components/HitFlashComponent
@onready var hurtbox_component: HurtboxComponent = $Components/HurtboxComponent
@onready var hitbox_component: HitboxComponent = $Components/HitboxComponent
@onready var circle_projectile_spawner: CircleProjectileSpawner = $Spawners/CircleProjectileSpawner
@onready var target_projectile_spawner: TargetProjectileSpawner = $Spawners/TargetProjectileSpawner
@onready var shoot_trigger: VisibleOnScreenNotifier2D = $ShootTrigger
@onready var components: ComponentContainer = $Components
@onready var spawners: ProjectileSpawnerContainer = $Spawners

func _ready() -> void:
	assert(worm_boss)
	
	self.hurtbox_component.health_component = worm_boss.health_component


func _process(delta: float) -> void:
	if not next_segment_joint:
		return
	
	self.global_position = lerp(
		self.global_position,
		next_segment_joint.global_position,
		move_speed * delta
	) 
	
	var target_angle = self.global_position.direction_to(
		next_segment.global_position
		).angle() - PI/2 
	
	self.rotation = lerp_angle(self.rotation, target_angle, delta * 100)


func explode() -> void:
	var vfx = CIRCLE_EXPLOSION.instantiate()
	vfx.global_position = self.global_position
	
	get_tree().current_scene.add_child(vfx)
	
	self.queue_free()
