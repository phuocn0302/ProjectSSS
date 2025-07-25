class_name HurtboxComponent
extends ComponentArea2D

@export var health_component: HealthComponent

func _ready() -> void:
	super._ready()
	assert(health_component != null, "[HurtboxComponent] HealthComponent is required")


func take_damage(amount: float):
	health_component.take_damage(amount)
