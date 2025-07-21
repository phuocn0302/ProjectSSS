class_name HealthComponent
extends Component

@export var max_health: float = 100
@export var current_health: float 
@export var die_sfx: PackedScene
@export var queue_free_on_die: bool = true

signal health_depleted(amount: float)
signal health_restored(amount: float)
signal health_reached_zero

func _ready() -> void:
	super._ready()
	current_health = max_health


func take_damage(damage_amount: float) -> void:
	current_health -= damage_amount
	health_depleted.emit(damage_amount)
	
	if current_health <= 0:
		current_health = 0
		health_reached_zero.emit()
		die()


func restore_health(heal_amount: float) -> void:
	var old_health := current_health
	current_health = clamp(current_health + heal_amount, 0, max_health)
	var restored := current_health - old_health
	if restored > 0:
		health_restored.emit(restored)


func die():
	if die_sfx:
		var sfx = die_sfx.instantiate()
		sfx.global_position = entity.global_position
		get_tree().current_scene.add_child(sfx)
	
	if queue_free_on_die:
		entity.queue_free()
