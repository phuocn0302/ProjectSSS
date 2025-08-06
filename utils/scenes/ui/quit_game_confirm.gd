extends Control

func _on_no_button_pressed() -> void:
	var tween = create_tween()
	tween.tween_property(self, "modulate:a", 0, 0.1)
	
	await tween.finished
	self.queue_free()


func _on_yes_button_pressed() -> void:
	get_tree().quit()
