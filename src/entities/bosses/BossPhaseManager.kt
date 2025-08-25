package entities.bosses

import commons.components.etc.HealthComponent
import commons.state_machine.State
import commons.state_machine.StateMachine
import entities.enemies.common_states.EnemyEntranceState
import entities.enemies.common_states.EnemyIdleState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node
import godot.core.VariantArray
import godot.core.connect
import godot.core.toGodotName
import godot.core.variantArrayOf
import godot.extension.getNodeAs
import godot.global.GD

@RegisterClass
class BossPhaseManager : Node() {

    @Export
    @RegisterProperty
    var healthComponent: HealthComponent? = null

    @Export
    @RegisterProperty
    var stateMachine: StateMachine? = null

    @Export
    @RegisterProperty
    var phaseData = variantArrayOf<BossPhaseData>()

    private var currentPhaseIndex: Int = -1
    private var bossIdleState: EnemyIdleState? = null
    private var bossEntranceState: EnemyEntranceState? = null

    @RegisterFunction
    fun setup() {
        GD.assert(healthComponent != null)
        GD.assert(stateMachine != null)

        phaseData.forEach { phaseData ->
            for (path in phaseData.states) {
                val node = getNodeOrNull(path)
                GD.assert(node is State, "Invalid State")
            }
        }

        phaseData = variantArrayOf(*phaseData.sortedBy { it.healthPercentage }.toTypedArray())

        stateMachine!!.getChildren().forEach {
            if (it is EnemyIdleState) {
                bossIdleState = it
            }

            if (it is EnemyEntranceState) {
                bossEntranceState = it
            }
        }

        GD.assert(bossIdleState != null)

        bossEntranceState?.onFinished?.connect {
            onEntranceFinished()
        } ?: onEntranceFinished()
    }

    @RegisterFunction
    fun onEntranceFinished() {
        healthComponent!!.healthDepleted.connect { _ ->
            updatePhase()
        }
        callDeferred("updatePhase".toGodotName())
    }

    @RegisterFunction
    fun updatePhase() {
        GD.print(owner!!.name, " health: ", healthComponent!!.currentHealth)

        val hpPercent = (healthComponent!!.currentHealth / healthComponent!!.maxHealth) * 100

        for (i in 0 until phaseData.size)
            if (hpPercent <= phaseData[i].healthPercentage) {
                if (currentPhaseIndex != i) {
                    currentPhaseIndex = i

                    bossIdleState!!.states = getStatesNode(i)

                    val firstStateNode = getNodeOrNull(phaseData[i].firstState)

                    if (firstStateNode is State) {
                        stateMachine!!.nextStateRequested = firstStateNode
                    }

                    GD.print("Phase changed to ", i, " with states: ", bossIdleState!!.states)
                }
                break
            }
    }

    private fun getStatesNode(index: Int) : VariantArray<State> {
        val states = variantArrayOf<State>()

        for (p in phaseData[index].states) {
            val node = this.getNodeAs<State>(p)
            if (node != null) {
                states.append(node)
            }
        }

        return states
    }

}