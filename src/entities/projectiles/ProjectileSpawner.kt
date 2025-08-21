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


@RegisterClass
open class ProjectileSpawner : Component2D() {

	@Export
	@RegisterProperty
	var spawnerData: ProjectileSpawnerData = ProjectileSpawnerData()

	@Export
	@RegisterProperty
	var projectileType: ProjectileFactory.Type = ProjectileFactory.Type.NORMAL

	@Export
	@RegisterProperty
	var projectileData: ProjectileData = ProjectileData()

	private var objectPool: ObjectPool = ObjectPool()
	private var timer: Timer = Timer()

	@RegisterFunction
	override fun _ready() {
		setupPool()
		setupTimer()

		active = active
	}

	@RegisterFunction
	override fun activate() {
		timer.start()
		super.activate()
	}

	@RegisterFunction
	override fun deactivate() {
		timer.stop()
		super.deactivate()
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
		timer.setName("SpawnInterval")
		this.addChild(timer)

		timer.timeout.connect(this, ProjectileSpawner::spawn)
		timer.processMode = Node.ProcessMode.PAUSABLE
		timer.waitTime = spawnerData.spawnInterval
		timer.autostart = true
		timer.start()
	}

}
