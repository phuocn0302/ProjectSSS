class_name ComponentContainer
extends Node2D

func get_component_list() -> Array[Node]:
	var arr: Array[Node] = []
	
	for c in self.get_children():
		if c is Component or c is Component2D or c is ComponentArea2D:
			arr.append(c)
			
	return arr


func disable_all_component() -> void:
	for c in get_component_list():
		c.active = false
