class_name EnemyDieState
extends State

@export var component_container: ComponentContainer
@export var proj_spawner_container: ProjectileSpawnerContainer

func enter() -> void:
	for p in get_tree().get_nodes_in_group("projectile"):
		if p is Projectile:
			p.deactive()
	
	for p in get_tree().get_nodes_in_group("warning_line"):
		if p is MoveWarningLine:
			p.hide()
	
	if component_container:
		component_container.disable_all_component()
	
	if proj_spawner_container:
		proj_spawner_container.disable_all_spawner()
	
	for s in state_machine.get_children():
		if not s == self:
			s.queue_free()
	
	state_machine.active = false
	
