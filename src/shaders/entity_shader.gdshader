shader_type canvas_item;

// === Outline Controls ===
uniform bool outline_enabled = false;
uniform vec4 outline_color    : source_color = vec4(1.0);
uniform float outline_size    : hint_range(0.0, 16.0, 0.1) = 1.0;
uniform float alpha_threshold : hint_range(0.0, 1.0, 0.01) = 0.0;
uniform int outline_samples   : hint_range(4, 8) = 4;

// === Flash Controls ===
uniform vec4 flash_color : source_color = vec4(1.0, 1.0, 1.0, 1.0);
uniform float flash_strength : hint_range(0.0, 1.0, 0.1) = 0.0;

// === Outline Sampling Directions ===
const vec2[8] DIRS = vec2[8](
	vec2(1.0, 0.0), vec2(0.0, 1.0),  vec2(-1.0, 0.0),  vec2(0.0, -1.0),
	vec2(1.0, 1.0), vec2(-1.0, 1.0), vec2(-1.0, -1.0), vec2(1.0, -1.0)
);

void fragment() {
	vec4 tex = texture(TEXTURE, UV);
	float alpha = tex.a;

	if (alpha <= 0.0 && outline_size <= 0.0) discard;

	bool solid = alpha > alpha_threshold;
	bool found_outline = false;

	// Only check outline if enabled and current pixel is transparent
	if (outline_enabled && !solid && outline_size > 0.0) {
		vec2 offset = TEXTURE_PIXEL_SIZE * outline_size;
		for (int i = 0; i < outline_samples; i++) {
			if (texture(TEXTURE, UV + DIRS[i] * offset).a > alpha_threshold) {
				found_outline = true;
				break;
			}
		}
	}

	// Color decision
	if (solid) {
		COLOR = tex;
	} else if (outline_enabled && found_outline) {
		COLOR = outline_color;
	} else {
		discard;
	}

	// === Flash Effect ===
	COLOR.rgb = mix(COLOR.rgb, flash_color.rgb, flash_strength);
}
