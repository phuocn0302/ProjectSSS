class_name ProjectileSpawnerContainer
extends Node2D

func get_spawner_list() -> Array[ProjectileSpawner]:
	var arr: Array[ProjectileSpawner] = []
	
	for s in self.get_children():
		if s is ProjectileSpawner:
			arr.append(s)
	
	return arr


func disable_all_spawner() -> void:
	for s in self.get_spawner_list():
		s.active = false
