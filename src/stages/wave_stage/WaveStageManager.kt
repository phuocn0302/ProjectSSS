package stages.wave_stage

import entities.enemies.Enemy
import entities.player.Player
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node2D
import godot.api.ProgressBar
import godot.api.Tween
import godot.api.Label
import godot.api.PackedScene
import godot.api.Timer
import godot.api.ConfigFile
import godot.core.Callable
import godot.core.Vector2
import godot.core.toGodotName
import godot.core.variantArrayOf
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.extension.getNodeAs
import godot.global.GD
import commons.singletons.Utils
import godot.api.CanvasLayer
import godot.api.InputEvent
import godot.core.connect
import godot.global.GD.load
import utils.scenes.ui.StageClear
import utils.scenes.ui.StageRetry

@RegisterClass
class WaveStageManager : Node2D() {

	companion object {
		private val DEFAULT_BOSS = load<PackedScene>("res://src/entities/bosses/godot/godot_boss.tscn")
		private val DEFAULT_ENEMY = load<PackedScene>("res://src/entities/enemies/godot_enemy/godot_enemy.tscn")
		private val STAGE_RETRY = load<PackedScene>("res://src/utils/scenes/ui/stage_retry.tscn")
	}

    @Export
    @RegisterProperty
    var waveStageData: WaveStageData? = null

    @Export
    @RegisterProperty
    var nextStage: PackedScene? = null

    private lateinit var player: Player
    private lateinit var canvasLayer: CanvasLayer
    private lateinit var bossHealthBar: ProgressBar
    private lateinit var playerHealthBar: ProgressBar
    private lateinit var scoreLabel: Label
    private lateinit var waveNumberLabel: Label
    private var waveTimer: Timer? = null
    private var enemySpawnTimer: Timer? = null
    private var currentWave: Int = 0
    private var wavesActive: Boolean = false
	private var score: Int = 0
	private var enemiesDefeated: Int = 0
	private var highScore: Int = 0

    @RegisterFunction
    override fun _ready() {
        player = getNodeAs("%Player")!!
        canvasLayer = getNodeAs("%CanvasLayer")!!
        bossHealthBar = getNodeAs("%BossHealthBar")!!
        playerHealthBar = getNodeAs("%PlayerHealthBar")!!
        scoreLabel = getNodeAs("%Score")!!
        waveNumberLabel = getNodeAs("%WaveNumber")!!


        bossHealthBar.visible = false
        playerHealthBar.visible = true
        playerHealthBar.maxValue = player.getMaxHealth()
        playerHealthBar.value = player.getMaxHealth()
		player.getHealthComp().healthDepleted.connect(Callable(this, "updatePlayerHealthBar".toGodotName()))
		player.onDefeated.connect(Callable(this, "onPlayerDie".toGodotName()))

		setupTimers()


		score = 0
		scoreLabel.text = "Score: $score"
		loadHighScore()
		val totalWavesInit = waveStageData?.waves?.size ?: 0
        waveNumberLabel.text = if (totalWavesInit > 0) "Wave 0/$totalWavesInit" else "Wave 0"

        // Wait for player input to begin waves
        setProcess(true)
    }

	@RegisterFunction
	override fun _process(delta: Double) {
		if (player.inputVector != Vector2.ZERO) {
			startNextWave()
			setProcess(false)
		}
	}
	}

	@RegisterFunction
	override fun _input(event: InputEvent?) {
		event?.let {
			if (it.isActionPressed("ui_cancel")) {
				showPauseMenu()
			}
		}
	}

    private fun setupTimers() {
        waveTimer = Timer().also { timer ->
            timer.setName("WaveTimer")
            timer.oneShot = true
            timer.timeout.connect(Callable(this, "onWaveEnded".toGodotName()))
            addChild(timer)
        }

        enemySpawnTimer = Timer().also { timer ->
            timer.setName("EnemySpawnTimer")
            timer.autostart = false
            timer.timeout.connect(Callable(this, "spawnEnemy".toGodotName()))
            addChild(timer)
        }
    }

    @RegisterFunction
    fun spawnEnemy() {
        val currentWaveData = getCurrentWaveData()
        val enemySceneToUse = currentWaveData?.enemyScene ?: DEFAULT_ENEMY
        val enemyInstance = enemySceneToUse?.instantiate() as? Enemy
        val spawnPos = currentWaveData?.enemySpawnPositions?.positions?.pickRandom() ?: Vector2(90.0, -30.0)
        enemyInstance?.globalPosition = spawnPos
        getTree()?.currentScene?.addChild(enemyInstance!!)

        enemyInstance?.onDefeated?.connect(
            Callable(this, "onEnemyDefeated".toGodotName()).bind(enemyInstance.getScorePoint())
        )
    }

    @RegisterFunction
    fun onEnemyDefeated(enemyScore: Int) {
        enemiesDefeated += 1
        score += enemyScore
        GD.print("Score: $score (kills: $enemiesDefeated)")
        scoreLabel.text = "Score: $score"
    }

    private fun getCurrentWaveData(): WaveData? {
        // currentWave is 1-based while arrays are 0-based
        val index = (currentWave - 1).coerceAtLeast(0)
        val waves = waveStageData?.waves ?: return null
        return if (index < waves.size) {
            waves[index]
        } else null
    }

    private fun startNextWave() {
        val totalWaves = waveStageData?.waves?.size ?: 0
        if (currentWave >= totalWaves) {
            spawnBoss()
            return
        }

        currentWave += 1
        GD.print("Starting wave #$currentWave")
        wavesActive = true

        // Update wave label
        val totalWavesLabel = waveStageData?.waves?.size ?: 0
        waveNumberLabel.text = if (totalWavesLabel > 0) "Wave $currentWave/$totalWavesLabel" else "Wave $currentWave"

        val currentWaveData = getCurrentWaveData()
        val waveDuration = currentWaveData?.waveDurationSeconds ?: 10.0
        val spawnInterval = currentWaveData?.spawnIntervalSeconds ?: 1.0

        // Configure timers for this wave
        waveTimer?.waitTime = waveDuration
        waveTimer?.start()

        enemySpawnTimer?.waitTime = spawnInterval
        enemySpawnTimer?.start()
    }

	@RegisterFunction
	fun onWaveEnded() {
		if (!wavesActive) return
		wavesActive = false

		enemySpawnTimer?.stop()
		GD.print("Wave #$currentWave ended")

		val totalWaves = waveStageData?.waves?.size ?: 0
		if (currentWave >= totalWaves) {
			spawnBoss()
			return
		}

		// Delay before starting the next wave
		val interWaveDelay = waveStageData?.interWaveDelaySeconds ?: 2.0
		if (interWaveDelay > 0.0) {
			val delay = Timer()
			delay.oneShot = true
			delay.waitTime = interWaveDelay
			addChild(delay)
			delay.start()
			delay.timeout.connect(Callable(this@WaveStageManager, "onInterWaveDelayElapsed".toGodotName()))
		} else {
			onInterWaveDelayElapsed()
		}
	}

    @RegisterFunction
    fun onInterWaveDelayElapsed() {
        startNextWave()
    }

    private fun spawnBoss() {
        // Stop any remaining spawning
        enemySpawnTimer?.stop()
        waveTimer?.stop()

        val bossScene = waveStageData?.bossScene ?: DEFAULT_BOSS
        val bossInstance = bossScene?.instantiate() as Enemy
        bossInstance.globalPosition = Vector2(90.0, -30.0)
        getTree()?.currentScene?.addChild(bossInstance)

		// Setup boss health bar with animation
		setupBossHealthBar(bossInstance.getMaxHealth())
		bossInstance.getHealthComp().healthDepleted.connect(
			Callable(this, "updateBossHealthBar".toGodotName()).bind(bossInstance)
		)
        bossInstance.onDefeated.connect(
            Callable(
                this,
                "onBossDefeated".toGodotName()
            ).bind(bossInstance.getScorePoint())
        )
    }

    @RegisterFunction
    fun setupBossHealthBar(maxHealth: Double) {
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
fun updateBossHealthBar(amount: Double, boss: Enemy) {
	bossHealthBar.value = boss.getHealthComp().currentHealth
}

    @RegisterFunction
    fun updatePlayerHealthBar(amount: Double) {
	playerHealthBar.value = player.getHealth()
    }

	@RegisterFunction
	fun onBossDefeated(points: Int) {
		score += points
		scoreLabel.text = "Score: $score"
		bossHealthBar.visible = false

		// Check and save high score
		if (score > highScore) {
			highScore = score
			saveHighScore()
			GD.print("New high score: $highScore")
		}

		enemySpawnTimer?.stop()
		waveTimer?.stop()

		val waitTween = Utils.createTweenTimer(this@WaveStageManager, 3.0)
		waitTween?.finished?.connect(Callable(this, "showStageClear".toGodotName()))
	}

    @RegisterFunction
    fun showStageClear() {
		val stageClearScene = load<PackedScene>("res://src/utils/scenes/ui/stage_clear.tscn")
		val ui = stageClearScene?.instantiate() as StageClear
		ui.setScore(score)
		ui.nextStage = nextStage
		val tree = getTree()
		tree?.currentScene?.queueFree()
		tree?.root?.addChild(ui)
		tree?.currentScene = ui
	}

	private fun loadHighScore() {
		val config = ConfigFile()
		val err = config.load("user://highscore.cfg")
		if (err == godot.core.Error.OK) {
			highScore = (config.getValue("game", "high_score", 0) as Long).toInt()
		}
	}

	private fun saveHighScore() {
		val config = ConfigFile()
		config.setValue("game", "high_score", highScore)
		config.save("user://highscore.cfg")
	}

	@RegisterFunction
	fun onPlayerDie() {
		// Stop all timers
		enemySpawnTimer?.stop()
		waveTimer?.stop()

		showRetryDialog()
	}

	@RegisterFunction
	fun showRetryDialog() {
		val retry = STAGE_RETRY?.instantiate() as? StageRetry
		retry?.yesPressed?.connect {
            onRetry()
        }
		retry?.noPressed?.connect {
            returnToTitle()
        }

		canvasLayer.addChild(retry!!)
	}

	@RegisterFunction
	fun onRetry() {
        GD.print("wtf")
		val tree = getTree()
		val currentScenePath = tree?.currentScene?.sceneFilePath
		if (currentScenePath != null) {
			tree?.changeSceneToFile(currentScenePath)
		} else {
			// Fallback to stage_1 if no scene path
			tree?.changeSceneToFile("res://src/stages/wave_stage/stage_1.tscn")
		}
	}

	@RegisterFunction
	fun returnToTitle() {
        GD.print("wtf")
		getTree()?.changeSceneToFile("res://src/stages/title_screen/title_screen.tscn")
	}

	@RegisterFunction
	fun showPauseMenu() {
		// Stop all timers
		enemySpawnTimer?.stop()
		waveTimer?.stop()

		// Show pause menu (you'll need to create this UI)
		// For now, just return to title
		getTree()?.changeSceneToFile("res://src/stages/title_screen/title_screen.tscn")
	}
}


