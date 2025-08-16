extends EnemyDieState

const CIRCLE_EXPLOSION = preload("res://particles/circle_explosion.tscn")

@onready var boss: WormBoss = $"../.."

func enter() -> void:
	super.enter()
	
	for s in boss.segments:
		s.components.disable_all_component()
		s.spawners.disable_all_spawner()
	
	await Utils.create_timer(2).timeout
	
	# Go back to org spot and make a desperate/fake ram
	var tween = create_tween().set_trans(Tween.TRANS_QUAD).set_ease(Tween.EASE_OUT)
	tween.set_parallel(true)
	tween.tween_property(boss, "global_rotation", 0, 0.5)
	tween.tween_property(boss, "global_position", boss.default_position, 1)
	tween.chain().set_parallel(false)
	tween.tween_property(boss, "global_position", boss.default_position + Vector2.UP * 30, 1)
	tween.tween_property(boss, "global_position", boss.default_position + Vector2.DOWN * 120, 0.3)
	
	for i in range(boss.segments.size() - 1, -1, -1):
		boss.segments[i].explode()
		await Utils.create_timer(0.1).timeout
	
	await Utils.create_timer(0.5).timeout
	
	var explosion = CIRCLE_EXPLOSION.instantiate()
	explosion.global_position = boss.global_position
	
	get_tree().current_scene.add_child(explosion)
	
	boss.queue_free()
