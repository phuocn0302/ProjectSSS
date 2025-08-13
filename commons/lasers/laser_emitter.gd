class_name LaserEmitter
extends Component2D

@export var laser_stats: LaserStats: set = _set_stats
@export var auto: bool = false: set = _set_auto
@export var screen_shake: bool = true

@export_group("Auto") 
@export var emit_interval: float = 3: set = _set_emit_interval

var timer: Timer
var laser: Laser
var _screen_shake_component: ScreenShakeComponent

func _ready() -> void:
	assert(laser_stats)
	
	if screen_shake and not _screen_shake_component:
		_screen_shake_component = ScreenShakeComponent.new()
		_screen_shake_component.shake_duration = 0.5
		self.add_child(_screen_shake_component)
	
	_setup_laser()


func emit_laser() -> void:
	if not laser: 
		return
	
	if screen_shake and _screen_shake_component:
		_screen_shake_component.shake()
	
	laser.cast()


func force_stop_emit() -> void:
	if not laser:
		return
	
	laser.stop_casting()


func _set_stats(val: LaserStats) -> void:
	laser_stats = val
	_setup_laser()


func _setup_laser() -> void:
	if not laser:
		laser = Laser.new()
		self.add_child(laser)
	
	laser.laser_stats = laser_stats


func _set_auto(val: bool) -> void:
	auto = val
	
	if val:
		_active_emit_timer()
	else:
		_deactive_emit_timer()


func _active_emit_timer() -> void:
	if not timer:
		timer = Timer.new()
		self.add_child(timer)
	
	if timer.is_stopped() and timer.is_inside_tree():
		timer.start(emit_interval)
	
	
	timer.wait_time = emit_interval
	timer.autostart = true
	
	if not timer.timeout.is_connected(emit_laser):
		timer.timeout.connect(emit_laser)


func _deactive_emit_timer() -> void:
	if not timer:
		return
	
	timer.timeout.disconnect(emit_laser)
	timer.stop()
	timer.queue_free()


func _set_emit_interval(val: float) -> void:
	emit_interval = val
	
	_active_emit_timer()
