package entities.bosses.godot

import commons.components.etc.HealthComponent
import commons.components.movements.IdleMoveComponent
import commons.components.sfx.GhostTrailingComponent
import commons.components.sfx.ScreenShakeComponent
import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.bosses.BossData
import entities.bosses.BossPhaseManager
import entities.enemies.Enemy
import entities.player.Player
import entities.projectiles.ProjectileSpawner
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.AnimatedSprite2D
import godot.api.GPUParticles2D
import godot.api.Marker2D
import godot.extension.getNodeAs

@RegisterClass
class GodotBoss : Enemy() {

	@Export
	@RegisterProperty
	var bossData: BossData = BossData()

	lateinit var rightArm: GodotBossArm
	lateinit var leftArm: GodotBossArm
	lateinit var rightArmOrgPos: Marker2D
	lateinit var leftArmOrgPos: Marker2D

	lateinit var animatedSprite2D: AnimatedSprite2D
	lateinit var eyeParticles: GPUParticles2D

	lateinit var targetProjectileSpawner: ProjectileSpawner
	lateinit var patternProjectileSpawner: ProjectileSpawner
	lateinit var circleProjectileSpawner: ProjectileSpawner

	lateinit var ghostTrailingComponent: GhostTrailingComponent
	lateinit var idleMoveComponent: IdleMoveComponent
	lateinit var screenShakeComponent: ScreenShakeComponent
	lateinit var healthComponent: HealthComponent

	lateinit var bossPhaseManager: BossPhaseManager
	lateinit var stateMachine: StateMachine

	var player: Player? = null

	private lateinit var dieState: State

	@RegisterFunction
	override fun _ready() {
		rightArm = getNodeAs("%RightArm")!!
		leftArm = getNodeAs("%LeftArm")!!
		rightArmOrgPos = getNodeAs("%RightArmPos")!!
		leftArmOrgPos = getNodeAs("%LeftArmPos")!!

		animatedSprite2D = getNodeAs("AnimatedSprite2D")!!
		eyeParticles = getNodeAs("%EyeParticles")!!

		targetProjectileSpawner = getNodeAs("%TargetProjectileSpawner")!!
		patternProjectileSpawner = getNodeAs("%PatternProjectileSpawner")!!
		circleProjectileSpawner = getNodeAs("%CircleProjectileSpawner")!!

		ghostTrailingComponent = getNodeAs("%GhostTrailingComponent")!!
		idleMoveComponent = getNodeAs("%IdleMoveComponent")!!
		screenShakeComponent = getNodeAs("%ScreenShakeComponent")!!
		healthComponent = getNodeAs("%HealthComponent")!!

		bossPhaseManager = getNodeAs("%BossPhaseManager")!!
		stateMachine = getNodeAs("%StateMachine")!!

		dieState = getNodeAs("%Die")!!

		bossPhaseManager.setup()
		stateMachine.setup(this)

		leftArm.hurtboxComponent.healthComponent = this.healthComponent
		rightArm.hurtboxComponent.healthComponent = this.healthComponent

		healthComponent.maxHealth = bossData.maxHealth
		healthComponent.healthReachedZero.connect(this, GodotBoss::onHealthReachZero)
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

	@RegisterFunction
	fun onHealthReachZero() {
		stateMachine.changeState(dieState)
	}

    override fun getMaxHealth(): Double {
        return bossData.maxHealth
    }

    override fun getHealthComp(): HealthComponent {
        return healthComponent
    }

    override fun getScorePoint(): Int {
        return 10100
    }
}
