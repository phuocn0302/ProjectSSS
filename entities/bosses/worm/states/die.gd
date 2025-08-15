extends WormBossState

const CIRCLE_EXPLOSION = preload("res://particles/circle_explosion.tscn")

@onready var components: Node2D = $"../../Components"
@onready var spawners: Node2D = $"../../Spawners"

func enter() -> void:
	for p in get_tree().get_nodes_in_group("projectile"):
		if p is Projectile:
			p.deactive()
	
	for p in get_tree().get_nodes_in_group("warning_line"):
		if p is MoveWarningLine:
			p.hide()
	
	for c in components.get_children():
		c.active = false
	
	for s in spawners.get_children():
		s.active = false
	
	for s in state_machine.get_children():
		if not s == self:
			s.queue_free()
	
	for s in boss.segments:
		for c in s.components.get_children():
			c.active = false
		
		for sp in s.spawners.get_children():
			sp.active = false
	
	state_machine.active = false
	
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
