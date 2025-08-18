class_name WormBoss
extends Enemy

signal defeated

const WORM_BOSS_BODY = preload("res://src/entities/bosses/worm/worm_boss_body.tscn")
const BODY_SPRITEFRAMES = preload("res://src/entities/bosses/worm/spriteframes/body_spriteframes.tres")
const TAIL_SPRITEFRAMES = preload("res://src/entities/bosses/worm/spriteframes/tail_spriteframes.tres")

@export var number_of_segment: int = 5

var player: Player
var default_position: Vector2
var segments: Array[WormBossBody]

@onready var joint_segment_pos: Marker2D = $JointSegmentPos
@onready var hurtbox_component: HurtboxComponent = $Components/HurtboxComponent
@onready var health_component: HealthComponent = $Components/HealthComponent
@onready var state_machine: StateMachine = $StateMachine
@onready var boss_entrance_state: EnemyEntranceState = $StateMachine/BossEntranceState
@onready var idle_move_component: IdleMoveComponent = $Components/IdleMoveComponent

func _ready() -> void:
	state_machine.setup(self)
	
	_spawn_segments()
	
	idle_move_component.active = false
	hurtbox_component.active = false

	await boss_entrance_state.finished
	
	idle_move_component.active = true
	hurtbox_component.active = true
	
	default_position = self.global_position


func _exit_tree() -> void:
	print("Worm defeated")
	defeated.emit()


func _process(delta: float) -> void:
	player = get_tree().get_first_node_in_group("player")
	
	state_machine.process_frame(delta)


func _physics_process(delta: float) -> void:
	state_machine.process_physics(delta)


func _spawn_segments() -> void:
	for i in range(number_of_segment):
		var s = WORM_BOSS_BODY.instantiate() as WormBossBody
		s.global_position = joint_segment_pos.global_position
		s.worm_boss = self
		
		self.health_component.health_depleted.connect(
			func(_t): if s: s.hit_flash_component.flash(_t)
		)
		
		get_tree().current_scene.add_child.call_deferred(s)
		
		segments.append(s)
	
	await get_tree().process_frame
	_connect_segments()
	
	await boss_entrance_state.finished
	for s in segments:
		s.hurtbox_component.active = true


func _connect_segments() -> void:
	# First body segment
	segments[0].next_segment_joint = self.joint_segment_pos
	segments[0].next_segment = self
	segments[0].sprite.sprite_frames = BODY_SPRITEFRAMES
	
	# The rest
	for i in range(1, segments.size()):
		segments[i].next_segment_joint = segments[i-1].joint_segment_pos
		segments[i].next_segment = segments[i-1]
		segments[0].sprite.sprite_frames = BODY_SPRITEFRAMES
		
	segments[-1].sprite.sprite_frames = TAIL_SPRITEFRAMES


func _on_health_component_health_reached_zero() -> void:
	state_machine.change_state($StateMachine/Die)
