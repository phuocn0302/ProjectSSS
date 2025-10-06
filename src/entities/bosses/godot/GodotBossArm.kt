package entities.bosses.godot

import commons.components.etc.HitboxComponent
import commons.components.etc.HurtboxComponent
import commons.components.movements.IdleMoveComponent
import commons.components.sfx.GhostTrailingComponent
import entities.Entity
import entities.enemies.Enemy
import godot.annotation.*
import godot.api.Marker2D
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.Tween
import godot.core.Vector2
import godot.core.connect
import godot.core.signal0
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD
import godot.global.GD.load


@RegisterClass
class GodotBossArm : Entity() {

	companion object {
		val CIRCLE_EXPLOSION = load<PackedScene>("res://src/particles/circle_explosion.tscn")
	}

	@RegisterSignal
	val onActivated by signal0()

	@RegisterSignal
	val onDestroyCompleted by signal0()

	@Export
	@RegisterProperty
	var activePosition: Marker2D? = null

	@Export
	@RegisterProperty
	var godotBoss: Enemy? = null

	lateinit var hurtboxComponent: HurtboxComponent
	lateinit var hitboxComponent: HitboxComponent
	lateinit var idleMoveComponent: IdleMoveComponent
	lateinit var ghostTrailingComponent: GhostTrailingComponent
	lateinit var explodePosition: Marker2D

	@RegisterFunction
	override fun _ready() {
		GD.assert(godotBoss != null)

		hurtboxComponent = getNodeAs("%HurtboxComponent")!!
		hitboxComponent = getNodeAs("%HitboxComponent")!!
		idleMoveComponent = getNodeAs("%IdleMoveComponent")!!
		ghostTrailingComponent = getNodeAs("%GhostTrailingComponent")!!
		explodePosition = getNodeAs("%ExplodePos")!!

		this.visible = false

		hurtboxComponent.active = false
		hitboxComponent.active = false
		idleMoveComponent.active = false

		this.globalPosition = godotBoss!!.globalPosition

	}

	@RegisterFunction
	fun activate() {
		visible = true
		val orgZIndex = zIndex
		zIndex = -99
		scale = Vector2.ZERO

		val tween = createTween()
		tween?.setTrans(Tween.TransitionType.SPRING)

		tween?.tweenProperty(
			this@GodotBossArm,
			"global_position",
			activePosition!!.globalPosition,
			1.0
		)

		tween?.tweenProperty(
			this@GodotBossArm,
			"scale",
			Vector2.ONE,
			1.0
		)

		tween?.finished?.connect {
			zIndex = orgZIndex

			hurtboxComponent.active = true
			hitboxComponent.active = true
			idleMoveComponent.active = true

			onActivated.emit()
		}
	}

	@RegisterFunction
	fun explode(position: Vector2? = null) {
		val vfx = CIRCLE_EXPLOSION!!.instantiate()

		if (vfx is Node2D) {
			vfx.globalPosition = position ?: explodePosition.globalPosition
		}
		getTree()?.currentScene?.addChild(vfx)
	}

	@RegisterFunction
	fun selfDestruct() = godotCoroutine(context = GodotDispatchers.MainThread) {
		hurtboxComponent.active = false
		hitboxComponent.active = false
		ghostTrailingComponent.active = false

		val tween = createTween()
		tween?.setTrans(Tween.TransitionType.SPRING)

		tween?.tweenProperty(
			this@GodotBossArm,
			"global_position",
			activePosition!!.globalPosition,
			3.0
		)

		tween?.finished?.await()

		explode(this@GodotBossArm.globalPosition)
		hide()

		onDestroyCompleted.emit()
	}
}
