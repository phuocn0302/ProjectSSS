package stages.demo_stage

import commons.singletons.Utils
import entities.enemies.Enemy
import entities.player.Player
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.CanvasLayer
import godot.api.Label
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.ProgressBar
import godot.api.Timer
import godot.api.Tween
import godot.core.Callable
import godot.core.Vector2
import godot.core.toGodotName
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD
import godot.global.GD.load
import utils.scenes.ui.StageRetry

@RegisterClass
class DemoStageManager : Node2D() {

    companion object {
        private val GODOT_BOSS = load<PackedScene>("res://src/entities/bosses/godot/godot_boss.tscn")
        private val GODOT_ENEMY = load<PackedScene>("res://src/entities/enemies/godot_enemy/godot_enemy.tscn")
        private val STAGE_RETRY = load<PackedScene>("res://src/utils/scenes/ui/stage_retry.tscn")
        private val DEMO_STAGE = load<PackedScene>("res://src/stages/demo_stage/demo_stage.tscn")
    }

    @Export
    @RegisterProperty
    var boss: PackedScene? = null

    @Export
    @RegisterProperty
    var enemySpawnPos = variantArrayOf<Vector2>()

    private lateinit var inputHint: Label
    private lateinit var enemySpawnTimer: Timer
    private lateinit var bossHealthBar: ProgressBar
    private lateinit var canvasLayer: CanvasLayer
    private lateinit var player: Player

    private var score: Int = 0

    @RegisterFunction
    override fun _ready() {
        inputHint = getNodeAs("%InputHint")!!
        enemySpawnTimer = getNodeAs("%EnemySpawnTimer")!!
        bossHealthBar = getNodeAs("%BossHealthBar")!!
        canvasLayer = getNodeAs("%CanvasLayer")!!
        player = getNodeAs("%Player")!!

        assert(enemySpawnPos.isNotEmpty())

        if (boss == null) {
            boss = GODOT_BOSS
        }

        enemySpawnTimer.autostart = false
        enemySpawnTimer.timeout.connect(Callable(this, "spawnEnemy".toGodotName()))

        bossHealthBar.visible = false
        player.treeExited.connect(Callable(this, "onPlayerDie".toGodotName()))

        // PauseManger.is_in_stage = true // TODO: Implement PauseManager
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        if (player.inputVector != Vector2.ZERO) {
            disableInputHint()
            enemySpawnTimer.start()
            setProcess(false)
        }
    }

    @RegisterFunction
    fun spawnEnemy() {
        val enemy = GODOT_ENEMY?.instantiate() as? entities.enemies.godot_enemy.GodotEnemy
        enemy?.globalPosition = enemySpawnPos.pickRandom()!!

        enemy?.onDefeated?.connect(Callable(this, "updateScore".toGodotName()).bind(1))

        getTree()?.currentScene?.addChild(enemy!!)
    }

    @RegisterFunction
    fun spawnBoss() = godotCoroutine(context = GodotDispatchers.MainThread) {
        val bossInstance = boss?.instantiate() as Enemy
        bossInstance.globalPosition = Vector2(90.0, -30.0)

        bossInstance.onDefeated.connect(Callable(this@DemoStageManager, "onBossDefeated".toGodotName()))

        getTree()?.currentScene?.addChild(bossInstance)

        setupHealthBar(bossInstance.getMaxHealth())

        bossInstance.getHealthComp().healthDepleted.connect(Callable(this@DemoStageManager, "updateHealthBar".toGodotName()))
    }

    @RegisterFunction
    fun updateScore(amount: Int) = godotCoroutine(context = GodotDispatchers.MainThread) {
        score += amount
        GD.print(score)

        if (enemySpawnTimer.timeout.isConnected(Callable(this@DemoStageManager, "spawnEnemy".toGodotName())) && score > 2) {
            enemySpawnTimer.timeout.disconnect(Callable(this@DemoStageManager, "spawnEnemy".toGodotName()))

            val waitTimer = Utils.createTweenTimer(this@DemoStageManager, 2.0)
            waitTimer?.finished?.await()
            spawnBoss()
        }
    }

    @RegisterFunction
    fun onBossDefeated() = godotCoroutine(context = GodotDispatchers.MainThread) {
        bossHealthBar.visible = false
        val waitTimer = Utils.createTweenTimer(this@DemoStageManager, 4.0)
        waitTimer?.finished?.await()

        returnToTitle()
    }

    @RegisterFunction
    fun setupHealthBar(maxHealth: Double) = godotCoroutine(context = GodotDispatchers.MainThread) {
        bossHealthBar.visible = true
        bossHealthBar.modulate.a = 0.0

        bossHealthBar.maxValue = maxHealth
        bossHealthBar.value = 0.0

        val tween = createTween()
        tween?.setEase(Tween.EaseType.OUT)?.setTrans(Tween.TransitionType.SINE)

        tween?.tweenProperty(bossHealthBar, "modulate:a", 1.0, 3.0)
        tween?.tweenProperty(bossHealthBar, "value", maxHealth, 2.0)
    }

    @RegisterFunction
    fun updateHealthBar(amount: Double) {
        bossHealthBar.value -= amount
    }

    @RegisterFunction
    fun disableInputHint() = godotCoroutine(context = GodotDispatchers.MainThread) {
        val waitTimer = Utils.createTweenTimer(this@DemoStageManager, 1.0)
        waitTimer?.finished?.await()
        val tween = createTween()
        tween?.tweenProperty(inputHint, "modulate:a", 0.0, 1.0)
    }

    @RegisterFunction
    fun onPlayerDie() = godotCoroutine(context = GodotDispatchers.MainThread) {
        // PauseManger.is_in_stage = false // TODO: Implement PauseManager
        val waitTimer = Utils.createTweenTimer(this@DemoStageManager, 1.0)
        waitTimer?.finished?.await()

        val retry = STAGE_RETRY?.instantiate() as? StageRetry

        retry?.yesPressed?.connect(Callable(this@DemoStageManager, "onRetry".toGodotName()))
        retry?.noPressed?.connect(Callable(this@DemoStageManager, "returnToTitle".toGodotName()))

        canvasLayer.addChild(retry!!)
    }

    @RegisterFunction
    fun onRetry() {
        val tree = getTree()
        val newStage = DEMO_STAGE?.instantiate() as? DemoStageManager
        newStage?.boss = boss

        tree?.currentScene?.queueFree()

        tree?.root?.addChild(newStage!!)
        tree?.currentScene = newStage
    }

    @RegisterFunction
    fun returnToTitle() {
        getTree()?.changeSceneToFile("res://src/stages/title_screen/title_screen.tscn")
    }
}
