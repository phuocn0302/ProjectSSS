package entities.enemies

import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.core.signal0

@RegisterClass
open class Enemy : Entity() {

    @RegisterSignal
    val onDefeated by signal0()

    @RegisterFunction
    override fun _enterTree() {
        addToGroup("enemy")
    }
}