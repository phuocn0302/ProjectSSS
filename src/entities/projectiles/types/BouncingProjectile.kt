package entities.projectiles.types

import commons.singletons.Utils
import entities.projectiles.Projectile
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class BouncingProjectile : Projectile() {

    @RegisterFunction
    override fun _process(delta: Double) {
        super._process(delta)
        checkBoundary()
    }

    private fun checkBoundary() {
        val size = this.projectileData?.collisionShape?.getRect()?.size

        val halfSize = size?.div(2) ?: return

        val hitRight = globalPosition.x + halfSize.x > Utils.SCREEN_SIZE.size.x
        val hitLeft = globalPosition.x - halfSize.x < 0
        val hitTop = globalPosition.y - halfSize.y < 0
        val hitBottom = globalPosition.y + halfSize.y > Utils.SCREEN_SIZE.size.y

        if (hitRight or hitLeft) {
            direction.x *= -1
        }

        if (hitTop or hitBottom) {
            direction.y *= -1
        }
    }
}