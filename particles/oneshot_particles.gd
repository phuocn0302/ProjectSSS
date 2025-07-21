class_name OneshotParticles
extends GPUParticles2D


func _ready() -> void:
	self.emitting = false
	self.one_shot = true
	self.emitting = true
	self.finished.connect(queue_free)
