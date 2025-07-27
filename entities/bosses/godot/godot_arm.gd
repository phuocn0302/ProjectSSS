class_name GodotArm
extends Entity

const CIRCLE_EXPLOSION = preload("res://particles/circle_explosion.tscn")

@export var godot_boss: GodotBoss
@export var active_position: Marker2D
@export var active_time: float = 2

@onready var hurtbox_component: HurtboxComponent = $HurtboxComponent
@onready var hitbox_component: HitboxComponent = $HitboxComponent
@onready var idle_move_component: IdleMoveComponent = $IdleMoveComponent
@onready var ghost_trailing_component: GhostTrailingComponent = $GhostTrailingComponent
@onready var explode_pos: Marker2D = $ExplodePos

func _ready() -> void:
	self.visible = false
	
	hurtbox_component.active = false
	hitbox_component.active = false
	idle_move_component.active = false
	
	global_position = godot_boss.position


func activate() -> void:
	self.visible = true
	var org_z_index = self.z_index
	self.z_index = -99
	self.scale = Vector2.ZERO
	
	var tween = create_tween().set_parallel(true)
	tween.set_trans(Tween.TRANS_SPRING)
	
	tween.tween_property(
		self,
		"global_position",
		active_position.global_position,
		active_time
	)
	
	tween.tween_property(
		self,
		"scale",
		Vector2.ONE,
		active_time
	)
	
	await tween.finished
	
	self.z_index = org_z_index
	hurtbox_component.active = true
	hitbox_component.active = true
	idle_move_component.active = true


func explode(pos := explode_pos.global_position) -> void:
	var sfx = CIRCLE_EXPLOSION.instantiate()
	sfx.global_position = pos
	
	get_tree().current_scene.add_child(sfx)


func self_destruct() -> void:
	hurtbox_component.active = false
	hitbox_component.active = false
	
	var tween = create_tween()
	tween.set_trans(Tween.TRANS_SPRING)
	
	tween.tween_property(
		self,
		"global_position",
		active_position.global_position,
		3
	)
	
	await tween.finished
	explode(self.global_position)
	hide()
