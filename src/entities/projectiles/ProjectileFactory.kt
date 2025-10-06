package entities.projectiles

import entities.projectiles.types.BouncingProjectile
import entities.projectiles.types.HomingProjectile
import entities.projectiles.types.SpawnerProjectile

object ProjectileFactory {

    enum class Type {
        NORMAL,
        HOMING,
        SPAWNER,
        BOUNCING,
    }

    fun createProjectile(data: ProjectileData, type: Type = Type.NORMAL): Projectile {
        val proj: Projectile = when (type) {
            Type.NORMAL -> Projectile()
            Type.HOMING -> HomingProjectile()
            Type.SPAWNER -> SpawnerProjectile()
            Type.BOUNCING -> BouncingProjectile()
        }
        proj.projectileData = data
        return proj
    }
}