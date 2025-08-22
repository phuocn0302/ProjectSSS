package entities.enemies

import entities.Entity
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
open class Enemy : Entity() {

    @RegisterFunction
    override fun _enterTree() {
        addToGroup("enemy")
    }
}