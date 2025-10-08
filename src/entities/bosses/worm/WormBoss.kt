package entities.bosses.worm

import commons.components.etc.HealthComponent
import commons.components.etc.HurtboxComponent
import commons.components.movements.IdleMoveComponent
import commons.components.sfx.ScreenShakeComponent
import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.bosses.BossData
import entities.bosses.BossPhaseManager
import entities.enemies.Enemy
import entities.enemies.common_states.EnemyEntranceState
import entities.player.Player
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Marker2D
import godot.api.PackedScene
import godot.api.GPUParticles2D
import godot.core.Vector2
import godot.core.connect
import godot.coroutines.await
import godot.extension.getNodeAs
import godot.core.toGodotName
import godot.global.GD.load

@RegisterClass
class WormBoss : Enemy() {

    companion object {
        val WORMBOSS_BODY = load<PackedScene>("res://src/entities/bosses/worm/worm_boss_body.tscn")
    }

    @Export
    @RegisterProperty
    var bossData: BossData = BossData()

    @Export
    @RegisterProperty
    var numberOfSegment: Int = 5

    lateinit var defaultPosition: Vector2
    lateinit var jointSegmentPos: Marker2D

    lateinit var hurtboxComponent: HurtboxComponent
    lateinit var healthComponent: HealthComponent
    lateinit var idleMoveComponent: IdleMoveComponent
    lateinit var screenShakeComponent: ScreenShakeComponent

    lateinit var bossPhaseManager: BossPhaseManager
    lateinit var stateMachine: StateMachine

    lateinit var bossEntranceState: EnemyEntranceState

    lateinit var patternProjectileSpawner: ProjectileSpawner
    lateinit var circleProjectileSpawner: ProjectileSpawner
    lateinit var targetProjectileSpawner: ProjectileSpawner
    lateinit var bounceProjSpawner: ProjectileSpawner
    lateinit var bounceShootChargeParticles: GPUParticles2D

    var player: Player? = null

    val segments = mutableListOf<WormBossBody>()

    @RegisterFunction
    override fun _ready() {
        jointSegmentPos = getNodeAs("%JointSegmentPos")!!

        hurtboxComponent = getNodeAs("%HurtboxComponent")!!
        healthComponent = getNodeAs("%HealthComponent")!!
        idleMoveComponent = getNodeAs("%IdleMoveComponent")!!
        screenShakeComponent = getNodeAs("%ScreenShakeComponent")!!

        bossPhaseManager = getNodeAs("%BossPhaseManager")!!
        stateMachine = getNodeAs("%StateMachine")!!

        bossEntranceState = getNodeAs("%BossEntranceState")!!

        // Spawners / effects
        patternProjectileSpawner = getNodeAs("%PatternProjectileSpawner")!!
        circleProjectileSpawner = getNodeAs("%CircleProjectileSpawner")!!
        targetProjectileSpawner = getNodeAs("%TargetProjectileSpawner")!!
        bounceProjSpawner = getNodeAs("%BounceProjSpawner")!!
        bounceShootChargeParticles = getNodeAs("%BounceShootChargeParticles")!!

        bossPhaseManager.setup()
        stateMachine.setup(this)

        spawnSegments()

        idleMoveComponent.active = false
        hurtboxComponent.active = false

        healthComponent.maxHealth = bossData.maxHealth
        healthComponent.healthReachedZero.connect(this, WormBoss::onHealthReachedZero)

        bossEntranceState.onFinished.connect {
            defaultPosition = this.globalPosition
            idleMoveComponent.active = true
            hurtboxComponent.active = true
        }
    }

    @RegisterFunction
    override fun _exitTree() {
        onDefeated.emit()
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        getTree()?.getFirstNodeInGroup("player")?.let { player = it as Player }
        stateMachine.frameProcess(delta)
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        stateMachine.physicsProcess(delta)
    }

    private fun spawnSegments() {
        for (i in 0 until numberOfSegment) {
            val body = WORMBOSS_BODY!!.instantiate() as WormBossBody
            body.globalPosition = Vector2.ZERO
            body.wormBoss = this

            healthComponent.healthDepleted.connect { _ ->
                if (godot.global.GD.isInstanceValid(body)) {
                    body.hitFlashComponent.flashNoArgs()
                }
            }

            // Defer add to ensure child's _ready runs before we connect
            getTree()?.currentScene?.callDeferred("add_child".toGodotName(), body)
            segments.add(body)
        }

        godot.coroutines.godotCoroutine(context = godot.coroutines.GodotDispatchers.MainThread) {
            getTree()?.processFrame?.await()
            connectSegments()

            bossEntranceState.onFinished.connect {
                segments.forEach { it.hurtboxComponent.active = true }
            }
        }
    }

    private fun connectSegments() {
        if (segments.isEmpty()) return

        // First body segment
        segments[0].nextSegmentJoint = jointSegmentPos
        segments[0].nextSegment = this

        // SpriteFrames are assigned in TSCN; skip here

        // The rest
        for (i in 1 until segments.size) {
            segments[i].nextSegmentJoint = segments[i - 1].jointSegmentPos
            segments[i].nextSegment = segments[i - 1]
        }
    }

    @RegisterFunction
    fun onHealthReachedZero() {
        val dieState = stateMachine.getNodeOrNull("Die") as? State
        if (dieState != null) stateMachine.changeState(dieState)
    }

    override fun getMaxHealth(): Double {
        return bossData.maxHealth
    }

    override fun getHealthComp(): HealthComponent {
        return healthComponent
    }

    override fun getScorePoint(): Int {
        return 14300
    }
}



