attribute vec3 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec3 a_normal;

uniform mat4 u_projTrans;
uniform vec4 u_color; //ambient
uniform mat4 u_trans;
uniform vec3 lightPos;
uniform vec3 lightPos2;
uniform vec3 cameraPos;
uniform int u_mirror; // 是否为倒影
uniform float u_horizon_y; // 地平线在哪一行
uniform float u_horizon_end_y; // 到哪里结束
uniform mat4 u_projMatrix;
uniform mat4 u_transMatrix;
uniform float u_widgetY;
uniform float u_movingLightX;
uniform float u_hiliteLightAlpha;

uniform float u_width;
uniform float u_height;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texCoords2;
varying vec2 v_texCoords3;
varying float v_position_y;
varying float v_horizon_y;
varying float v_horizon_end_y;
varying float v_angle;

void main() {
	vec4 transPos = u_trans * vec4(a_position, 1.0);
	v_color =  a_color;

	v_texCoords = a_texCoord0;
	gl_Position = u_projTrans * vec4(a_position, 1.0);;

	v_texCoords2 = vec2(transPos.x / u_width, transPos.y / u_height);
}
