#ifdef GL_ES
#define LOWP lowp
precision mediump float;
precision highp int;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texCoords2;

uniform float u_parentAlpha;
uniform sampler2D u_texture;
uniform sampler2D u_texture_env;


const vec4 a = vec4(0.5, 0.5, 0.5, 1);
const vec4 WHITE = vec4(1, 1, 1, 1);

void main() {
	vec4 color = v_color * texture2D(u_texture, v_texCoords);
	vec4 blendColor = vec4(0.0, 0.0, 0.0, 0.0);

	blendColor = texture2D(u_texture_env, v_texCoords2) *0.4;


	// blendColor.a = 1.0;
	color += blendColor;

	// parent aplha
	if (u_parentAlpha != 1.0)  {
		color.a = color.a * u_parentAlpha;
	}

	gl_FragColor = color;
}
