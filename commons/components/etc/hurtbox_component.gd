class_name HurtboxComponent
extends ComponentArea2D

signal hurt

@export var health_component: HealthComponent

func take_damage(amount: float):
	if not health_component:
		push_error("No health component")
		return
	
	health_component.take_damage(amount)
	hurt.emit()
