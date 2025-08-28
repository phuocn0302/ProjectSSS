package entities.projectiles

import commons.components.Component2D
import commons.object_pooling.ObjectPool
import entities.projectiles.spawners.ProjectileSpawnerData
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node
import godot.api.Timer
import godot.global.GD


@RegisterClass
open class ProjectileSpawner : Component2D() {

	@Export
	@RegisterProperty
	var spawnerData: ProjectileSpawnerData = ProjectileSpawnerData()
		set(value) {
			field = value
			setupTimer()
		}

	@Export
	@RegisterProperty
	var projectileType: ProjectileFactory.Type = ProjectileFactory.Type.NORMAL

	@Export
	@RegisterProperty
	var projectileData: ProjectileData = ProjectileData()

	private var objectPool: ObjectPool = ObjectPool()
	private var timer: Timer? = null

	@RegisterFunction
	override fun _ready() {
		setupPool()
		setupTimer()

		active = active
	}

	@RegisterFunction
	override fun activate() {
		timer?.start()
		super.activate()
	}

	@RegisterFunction
	override fun deactivate() {
		timer?.stop()
		super.deactivate()
	}

	@RegisterFunction
	fun setInterval(amount: Double) {
		val spawner = this.spawnerData
		spawner.spawnInterval = amount
		spawnerData = spawner
	}

	@RegisterFunction
	open fun spawn() {
		spawnerData.spawn(objectPool, this)
	}

	private fun setupPool() {
		objectPool.setName("ProjectilePool")

		this.addChild(objectPool)

		val proj = ProjectileFactory.createProjectile(projectileData, projectileType)
		objectPool.setupNode(proj)
	}

	private fun setupTimer() {
		if (timer == null) {
			timer = Timer()

			timer!!.setName("SpawnInterval")
			timer!!.timeout.connect(this, ProjectileSpawner::spawn)
			timer!!.processMode = Node.ProcessMode.PAUSABLE

			this.addChild(timer)
		}

		timer?.waitTime = spawnerData.spawnInterval

		timer?.let {
			it.waitTime = spawnerData.spawnInterval
			it.autostart = true

			if (it.isInsideTree()) {
				it.stop()
				it.start()
			}
		}
	}
}
