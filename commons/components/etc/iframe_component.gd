class_name IFrameComponent
extends Component

signal iframe_activated

@export var hurtbox_component: HurtboxComponent
@export var iframe_duration: float = 0.3

func _ready() -> void:
	super._ready()
	
	assert(hurtbox_component)
	hurtbox_component.hurt.connect(start_iframe)


func start_iframe(duration: float = -1) -> void:
	hurtbox_component.active = false
	
	iframe_activated.emit()
	
	if duration == -1:
		duration = iframe_duration
	
	await get_tree().create_timer(duration).timeout
	hurtbox_component.active = true
