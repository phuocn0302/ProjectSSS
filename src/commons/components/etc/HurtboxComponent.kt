package commons.components.etc

import commons.components.ComponentArea2D
import godot.annotation.*
import godot.core.signal0
import godot.global.GD

@RegisterClass
class HurtboxComponent : ComponentArea2D() {

    @RegisterSignal
    val hurt by signal0()

    @RegisterProperty
    @Export
    var healthComponent: HealthComponent? = null

    @RegisterFunction
    fun takeDamage(amount: Double) {
        val hc = healthComponent
        if (hc == null) {
            GD.pushError("No health component")
            return
        }

        hc.takeDamage(amount)
        hurt.emit()
    }
}
