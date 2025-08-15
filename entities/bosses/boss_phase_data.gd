class_name BossPhaseData
extends Resource

## Trigger phase when boss current health below this percentage
@export_range(0, 100) var health_percentage: float = 100
@export var states: Array[NodePath]
@export var first_state: NodePath
