class_name DemoStageManager
extends Node2D

const GODOT_BOSS = preload("res://entities/bosses/godot/godot_boss.tscn")
const GODOT_ENEMY = preload("res://entities/enemies/godot_enemy/godot_enemy.tscn")

const STAGE_RETRY = preload("res://utils/scenes/ui/stage_retry.tscn")
const DEMO_STAGE = preload("res://stages/demo_stage/demo_stage.tscn")

@export var boss: PackedScene
@export var enemy_spawn_pos: Array[Vector2]

@onready var input_hint: Label = $CanvasLayer/InputHint
@onready var enemy_spawn_timer: Timer = $EnemySpawnTimer
@onready var boss_health_bar: ProgressBar = $CanvasLayer/BossHealthBar
@onready var canvas_layer: CanvasLayer = $CanvasLayer

@onready var player: Player = $Player

var _score: int = 0

func _ready() -> void:
	assert(not enemy_spawn_pos.is_empty())
	
	if not boss:
		boss = GODOT_BOSS
	
	enemy_spawn_timer.autostart = false
	enemy_spawn_timer.timeout.connect(_spawn_enemy)
	
	boss_health_bar.visible = false
	player.die.connect(_on_player_die)
	
	PauseManger.is_in_stage = true


func _process(_delta: float) -> void:
	if player.input_vector != Vector2.ZERO:
		disable_input_hint()
		enemy_spawn_timer.start()
		set_process(false)


func _spawn_enemy() -> void:
	var e = GODOT_ENEMY.instantiate() as GodotEnemy
	e.global_position = enemy_spawn_pos.pick_random()
	
	e.defeated.connect(Callable(self, "_update_score").bind(1))
	
	get_tree().current_scene.add_child(e)


func _spawn_boss() -> void:
	var _boss = boss.instantiate()
	_boss.global_position = Vector2(90,-30)
	
	_boss.defeated.connect(_on_boss_defeated)
	
	get_tree().current_scene.add_child(_boss)
	
	_setup_health_bar(_boss.health_component.max_health)
	
	_boss.health_component.health_depleted.connect(_update_health_bar)


func _update_score(amount: int) -> void:
	_score += amount
	print(_score)
	
	if enemy_spawn_timer.timeout.is_connected(_spawn_enemy) and _score > 7:
		enemy_spawn_timer.timeout.disconnect(_spawn_enemy)
		
		await Utils.create_timer(2).timeout
		_spawn_boss()


func _on_boss_defeated() -> void:
	boss_health_bar.visible = false
	await Utils.create_timer(1).timeout
	
	_return_to_title()
	


func _setup_health_bar(max_health: float) -> void:
	boss_health_bar.visible = true
	boss_health_bar.modulate.a = 0
	
	boss_health_bar.max_value = max_health
	boss_health_bar.value = 0
	
	var tween = create_tween()
	tween.set_ease(Tween.EASE_OUT).set_trans(Tween.TRANS_SINE)
	
	tween.tween_property(
		boss_health_bar,
		"modulate:a",
		1,
		3
	)
	
	tween.tween_property(
		boss_health_bar,
		"value",
		max_health,
		2
	)


func _update_health_bar(amount: float) -> void:
	boss_health_bar.value -= amount


func disable_input_hint() -> void:
	await Utils.create_timer(1).timeout
	var tween = create_tween()
	tween.tween_property(input_hint, "modulate:a", 0, 1)


func _on_player_die() -> void:
	PauseManger.is_in_stage = false
	await Utils.create_timer(1).timeout
	
	var retry = STAGE_RETRY.instantiate()
	
	retry.yes_pressed.connect(_on_retry)
	retry.no_pressed.connect(_return_to_title)
	
	canvas_layer.add_child(retry)


func _on_retry() -> void:
	var tree = get_tree()
	var new_stage = DEMO_STAGE.instantiate() as DemoStageManager
	new_stage.boss = boss

	if tree.current_scene:
		tree.current_scene.queue_free()

	tree.root.add_child(new_stage)
	tree.current_scene = new_stage


func _return_to_title() -> void:
	# Dont know why change scene to packed isnt working here
	get_tree().change_scene_to_file("res://stages/title_screen/title_screen.tscn")
