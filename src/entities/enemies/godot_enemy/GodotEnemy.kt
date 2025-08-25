package entities.enemies.godot_enemy

import entities.enemies.Enemy
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.core.signal0

@RegisterClass
class GodotEnemy : Enemy() {

    @RegisterFunction
    override fun _exitTree() {
        onDefeated.emit()
    }
}