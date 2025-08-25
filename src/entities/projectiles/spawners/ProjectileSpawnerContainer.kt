package entities.projectiles.spawners

import entities.projectiles.ProjectileSpawner
import godot.api.Node2D
import godot.core.VariantArray
import godot.core.variantArrayOf

class ProjectileSpawnerContainer : Node2D() {

	fun getSpawnersList() : VariantArray<ProjectileSpawner>? {
		val arr = variantArrayOf<ProjectileSpawner>()

		for (s in getChildren().filterIsInstance<ProjectileSpawner>()) {
			arr.append(s)
		}

		return arr
	}

	fun disableAllSpawner() {
		val spawners = getSpawnersList()
		spawners?.let {
		   for (s in spawners) {
			   s.active = false
		   }
		}
	}
}
