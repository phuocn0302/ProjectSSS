class_name MoveWarningLine
extends Line2D

const MED_MOVE_WARNING_LINE = preload("res://assets/texture/sfx/line/med_move_warning_line.png")

@export var line_texture: Texture2D = MED_MOVE_WARNING_LINE
@export var show_time: float = 0.5
@export var direction: Vector2 = Vector2.DOWN
@export var length: float = 320

var start_point: Vector2
var end_point: Vector2

func _ready() -> void:
	self.texture = line_texture
	self.texture_mode = Line2D.LINE_TEXTURE_TILE
	self.texture_repeat = CanvasItem.TEXTURE_REPEAT_ENABLED
	self.z_index = -999
	self.points = [Vector2.ZERO, Vector2.ZERO]
	
	self.visibility_changed.connect(_on_visibility_change)


func _process(_delta: float) -> void:
	if not self.visible:
		return
	
	self.points[1] = end_point


func _on_visibility_change() -> void:
	if not self.visible:
		end_point = Vector2.ZERO
		points = [Vector2.ZERO, Vector2.ZERO]
		return
	
	var tween = create_tween()
	tween.tween_property(self, "end_point", direction.normalized() * length, show_time)
