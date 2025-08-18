extends Control

const BOSS_ENTRY = preload("res://src/stages/demo_boss_selector/boss_entry.tscn")
const DEMO_STAGE = preload("res://src/stages/demo_stage/demo_stage.tscn")

@export var bosses_data: Array[BossEntryData]

@onready var entry_container: VBoxContainer = %EntryContainer

func _ready() -> void:
	assert(bosses_data)
	_add_boss_entry()


func _input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel"):
		_on_return_button_pressed()


func _add_boss_entry() -> void:
	var boss_entries: Array[Control]
	for b in bosses_data:
		var entry = BOSS_ENTRY.instantiate()
		
		entry.boss_name = b.boss_name
		entry.boss_mugshot = b.boss_mugshot
		
		entry.mouse_entered.connect(func(): entry.grab_focus())
		entry.pressed.connect(Callable(self,"_on_stages_selected").bind(b.boss_scene))
		
		entry_container.add_child(entry)
		boss_entries.append(entry)
	
	if not boss_entries.is_empty():
		boss_entries[0].grab_focus()


func _on_stages_selected(boss: PackedScene) -> void:
	var stage = DEMO_STAGE.instantiate() as DemoStageManager
	stage.boss = boss

	var tree = get_tree()
	if tree.current_scene:
		tree.current_scene.queue_free()

	tree.root.add_child(stage)
	tree.current_scene = stage


func _on_return_button_pressed() -> void:
	var tween = create_tween()
	tween.tween_property(self, "modulate:a", 0, 0.1)
	
	await tween.finished
	
	self.queue_free()
