varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;

void main() {
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color;
	if (color.a != 0) {
		color.r = 0;
		color.g = 0;
		color.b = 1;
		color.a = 1;
		gl_FragColor = color;
	}
}
