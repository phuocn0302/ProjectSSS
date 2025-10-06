package entities.bosses.capy

import commons.MoveWarningLine
import commons.components.etc.HealthComponent
import commons.components.movements.IdleMoveComponent
import commons.components.sfx.GhostTrailingComponent
import commons.components.sfx.ScreenShakeComponent
import commons.lasers.LaserEmitter
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
import godot.api.*
import godot.core.Vector2
import godot.core.connect
import godot.extension.getNodeAs

@RegisterClass
class CapyBoss : Enemy() {

	@Export
	@RegisterProperty
	var bossData: BossData = BossData()

	lateinit var gun: AnimatedSprite2D
	lateinit var mainSprite: Sprite2D
	lateinit var gpuParticles2D: GPUParticles2D
	lateinit var warningLine: Line2D
	lateinit var moveWarningLine: MoveWarningLine

	lateinit var leftMissileSpawner: ProjectileSpawner
	lateinit var rightMissileSpawner: ProjectileSpawner
	lateinit var patternProjectileSpawner: ProjectileSpawner
	lateinit var circleProjectileSpawner: ProjectileSpawner
	lateinit var laserEmitter: LaserEmitter

	lateinit var ghostTrailingComponent: GhostTrailingComponent
	lateinit var idleMoveComponent: IdleMoveComponent
	lateinit var screenShakeComponent: ScreenShakeComponent
	lateinit var healthComponent: HealthComponent

	lateinit var bossPhaseManager: BossPhaseManager
	lateinit var stateMachine: StateMachine

	lateinit var defaultPosition: Vector2

	var player: Player? = null

	private lateinit var dieState: State
	private lateinit var entranceState: EnemyEntranceState

	@RegisterFunction
	override fun _ready() {
		gun = getNodeAs("%Gun")!!
		mainSprite = getNodeAs("%Main")!!
		gpuParticles2D = getNodeAs("%GPUParticles2D")!!
		warningLine = getNodeAs("%WarningLine")!!
		moveWarningLine = getNodeAs("%MoveWarningLine")!!

		leftMissileSpawner = getNodeAs("%LeftMissileSpawner")!!
		rightMissileSpawner = getNodeAs("%RightMissileSpawner")!!
		circleProjectileSpawner = getNodeAs("%CircleProjectileSpawner")!!
		patternProjectileSpawner = getNodeAs("%PatternProjectileSpawner")!!
		laserEmitter = getNodeAs("%LaserEmitter")!!

		ghostTrailingComponent = getNodeAs("%GhostTrailingComponent")!!
		idleMoveComponent = getNodeAs("%IdleMoveComponent")!!
		screenShakeComponent = getNodeAs("%ScreenShakeComponent")!!
		healthComponent = getNodeAs("%HealthComponent")!!

		bossPhaseManager = getNodeAs("%BossPhaseManager")!!
		stateMachine = getNodeAs("%StateMachine")!!

		dieState = getNodeAs("%Die")!!
		entranceState = getNodeAs("%EnemyEntranceState")!!

		bossPhaseManager.setup()
		stateMachine.setup(this)

		idleMoveComponent.active = false
		ghostTrailingComponent.active = false

		entranceState.onFinished.connect {
			defaultPosition = this.globalPosition
			ghostTrailingComponent.active = true
			idleMoveComponent.active = true
		}

		healthComponent.maxHealth = bossData.maxHealth
		healthComponent.healthReachedZero.connect(this, CapyBoss::onHealthReachZero)
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
	fun recoilGun() {
		screenShakeComponent.shake()

		val startPos = gun.position
		val tween = createTween()
		tween?.let {
			it.setTrans(Tween.TransitionType.SPRING)
			it.tweenProperty(gun, "position", startPos + Vector2.UP * 10, 0.1)
			it.tweenProperty(gun, "position", startPos, 1.0)
		}
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
}
