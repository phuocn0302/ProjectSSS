class_name BossEntranceState
extends State

signal finished

@export var speed: float = 20
@export var destination_point: Vector2 = Vector2(90,60)
@export var hurtbox_component: HurtboxComponent

var ended: bool = false

func enter() -> void:
	assert(hurtbox_component)
	hurtbox_component.active = false


func exit() -> void:
	hurtbox_component.active = true


func process_frame(_delta: float) -> void:
	if not ended and actor.global_position.distance_squared_to(destination_point) < 0.5:
		ended = true
		finished.emit()


func process_physics(delta: float) -> void:
	actor.global_position = actor.global_position.move_toward(
		destination_point,
		speed * delta
	) 
