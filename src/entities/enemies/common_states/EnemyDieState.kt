package entities.enemies.common_states

import commons.MoveWarningLine
import commons.components.ComponentContainer
import commons.state_machine.State
import entities.projectiles.Projectile
import entities.projectiles.spawners.ProjectileSpawnerContainer
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty

@RegisterClass
open class EnemyDieState : State() {

    @Export
    @RegisterProperty
    var componentContainer: ComponentContainer? = null

    @Export
    @RegisterProperty
    var projectileSpawnerContainer: ProjectileSpawnerContainer? = null

    @RegisterFunction
    override fun enter() {
        getTree()
            ?.getNodesInGroup("projectile")
            ?.filterIsInstance<Projectile>()
            ?.forEach { projectile -> projectile.deactivate() }

        getTree()
            ?.getNodesInGroup("warning_line")
            ?.filterIsInstance<MoveWarningLine>()
            ?.forEach { moveWarningLine -> moveWarningLine.hide() }

        componentContainer?.disableAllComponents()

        projectileSpawnerContainer?.disableAllSpawner()

        stateMachine.getChildren().filterIsInstance<State>().forEach { state ->
            if (state != this) {
                state.queueFree()
            }
        }

        stateMachine.active = false
    }
}