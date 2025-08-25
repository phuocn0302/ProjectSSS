package entities.bosses.godot.states

import entities.bosses.godot.GodotBossState
import godot.annotation.Export
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.coroutines.GodotDispatchers
import godot.coroutines.await
import godot.coroutines.godotCoroutine
import godot.global.GD

@RegisterClass
class GodotBossPhase2TransitionState : GodotBossState() {

    @Export
    @RegisterProperty
    var nextState: GodotBossState? = null

    @RegisterFunction
    override fun enter() = godotCoroutine(context = GodotDispatchers.MainThread) {
        with(boss) {
            screenShakeComponent.shake()

            leftArm.activate()
            leftArm.onActivated.await()

            screenShakeComponent.shake()

            rightArm.activate()
            rightArm.onActivated.await()

            eyeParticles.emitting = true
            ghostTrailingComponent.active = true
            idleMoveComponent.active = true
            GD.print(nextState)
        }

        GD.assert(nextState != null)
        stateMachine.changeState(nextState!!)
        GD.print(nextState)
    }

}