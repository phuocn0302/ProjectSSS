package stages.demo_boss_selector

import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Control
import godot.api.InputEvent
import godot.api.PackedScene
import godot.api.VBoxContainer
import godot.core.Callable
import godot.core.connect
import godot.core.toGodotName
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD

@RegisterClass
class DemoBossSelector : Control() {

	companion object {
		private val BOSS_ENTRY = GD.load<PackedScene>("res://src/stages/demo_boss_selector/boss_entry.tscn")
		private val DEMO_STAGE = GD.load<PackedScene>("res://src/stages/demo_stage/demo_stage.tscn")
	}

	@Export
	@RegisterProperty
	var bossesData = variantArrayOf<BossEntryData>()

	private lateinit var entryContainer: VBoxContainer

	@RegisterFunction
	override fun _ready() {
		entryContainer = getNodeAs("%EntryContainer")!!

		assert(bossesData.isNotEmpty())
		addBossEntry()
	}

	@RegisterFunction
	override fun _input(event: InputEvent?) {
		event?.let {
			if (it.isActionPressed("ui_cancel")) {
				onReturnButtonPressed()
			}
		}
	}

	@RegisterFunction
	fun addBossEntry() {
		val bossEntries = mutableListOf<Control>()
		for (bossData in bossesData) {
			val entry = BOSS_ENTRY?.instantiate() as? BossEntry

			entry?.bossName = bossData.bossName
			entry?.bossMugshot = bossData.bossMugshot

			entryContainer.addChild(entry!!)
			
			entry?.mouseEntered?.connect { entry.grabFocus() }
			entry?.pressed?.connect(Callable(this, "onStagesSelected".toGodotName()).bind(bossData.bossScene))

			bossEntries.add(entry)
		}

		if (bossEntries.isNotEmpty()) {
			bossEntries[0].grabFocus()
		}
	}

	@RegisterFunction
	fun onStagesSelected(boss: PackedScene) {
		val stage = DEMO_STAGE?.instantiate() as? stages.demo_stage.DemoStageManager
		stage?.boss = boss

		val tree = getTree()
		tree?.currentScene?.queueFree()

		tree?.root?.addChild(stage!!)
		tree?.currentScene = stage
	}

	@RegisterFunction
	fun onReturnButtonPressed() = godotCoroutine(context = GodotDispatchers.MainThread) {
		val tween = createTween()
		tween?.tweenProperty(this@DemoBossSelector, "modulate:a", 0.0, 0.1)

		tween?.finished?.await()

		queueFree()
	}
}
