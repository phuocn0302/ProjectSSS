extends TextureButton

@export var boss_name: String
@export var boss_mugshot: Texture2D

@onready var boss_name_label: Label = $BossName
@onready var boss_mugshot_sprite: Sprite2D = $BossMugshot

func _ready() -> void:
	assert(boss_name and boss_mugshot)
	assert(boss_mugshot.get_size() == Vector2(30,30))
	
	boss_name_label.text = boss_name
	boss_mugshot_sprite.texture = boss_mugshot
