package entities.projectiles.spawners

import commons.object_pooling.ObjectPool
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node2D
import godot.core.Vector2

@RegisterClass
class TargetProjectileSpawnerData : ProjectileSpawnerData() {

    @Export
    @RegisterProperty
    var targetGroup: String = "player"

    @RegisterFunction
    override fun spawn(projectilePool: ObjectPool, spawner: ProjectileSpawner) {
        setDirection(spawner)
        super.spawn(projectilePool, spawner)
    }

    private fun setDirection(spawner: ProjectileSpawner) {
        val targets = spawner.getTree()?.getNodesInGroup(targetGroup)
            ?.filterIsInstance<Node2D>()
            ?: return

        val closest = getClosestTarget(targets, spawner.globalPosition) ?: return
        spawnDirection = (closest.globalPosition - spawner.globalPosition).normalized()
    }

    private fun getClosestTarget(targets: List<Node2D>, spawnPosition: Vector2): Node2D? {
        var closest: Node2D? = null
        var minDist = Double.POSITIVE_INFINITY

        for (t in targets) {
            val dist = spawnPosition.distanceSquaredTo(t.globalPosition)
            if (dist < minDist) {
                minDist = dist
                closest = t
            }
        }

        return closest
    }
}