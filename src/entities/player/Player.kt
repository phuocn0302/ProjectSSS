package entities.player

import commons.components.etc.HealthComponent
import commons.state_machine.StateMachine
import entities.Entity
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.OS
import godot.core.Vector2
import godot.core.connect
import godot.core.signal0
import godot.core.variantArrayOf
import godot.extension.getNodeAs

@RegisterClass
class Player : Entity() {

    @RegisterSignal
    val onDefeated by signal0()

    @Export
    @RegisterProperty
    var playerData: PlayerData = PlayerData()

    @RegisterProperty
    var inputVector:  Vector2 = Vector2.ZERO

    private lateinit var projectileSpawner: ProjectileSpawner

    private lateinit var stateMachine: StateMachine
    private lateinit var inputHandler: PlayerInputHandler
    private lateinit var mobileInputHandler: MobileInputHandler
    private lateinit var healthComponent: HealthComponent

    @RegisterFunction
    override fun _enterTree() {
        addToGroup("player")
    }

    @RegisterFunction
    override fun _ready() {
        projectileSpawner = getNodeAs("%ProjectileSpawner")!!
        healthComponent = getNodeAs("%HealthComponent")!!

        stateMachine = getNodeAs("StateMachine")!!

        healthComponent.healthReachedZero.connect {
            projectileSpawner.active = false
            onDefeated.emit()

        }
        inputHandler = getNodeAs("%InputHandler")!!
        mobileInputHandler = getNodeAs("%MobileInputHandler")!!

        setupInputHandler()

        stateMachine.setup(this)
    }

    private fun setupInputHandler() {
        if (OS.getName() in variantArrayOf("Android", "iOS")) {
            mobileInputHandler.setup(this, stateMachine)

            val spawnerData = this.projectileSpawner.spawnerData
            spawnerData.spawnInterval = playerData.shootInterval

            projectileSpawner.spawnerData = spawnerData
            projectileSpawner.active = true
        } else {
            inputHandler.setup(this, stateMachine)
        }
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        stateMachine.frameProcess(delta)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        stateMachine.physicsProcess(delta)
    }

    fun getMaxHealth() : Double {
        return healthComponent.maxHealth
    }

    fun getHealth() : Double {
        return healthComponent.currentHealth
    }

    fun getHealthComp() : HealthComponent {
        return healthComponent
    }
}