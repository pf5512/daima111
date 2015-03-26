#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec4 vAmbient;//接收从顶点着色器过来的环境光分量
varying vec4 vDiffuse;//接收从顶点着色器过来的散射光分量
varying vec4 vSpecular;//接收从顶点着色器过来的镜面反射光分量
uniform sampler2D u_texture;
void main()
{
  vec4 finalColor=v_color * texture2D(u_texture, v_texCoords);
  gl_FragColor=finalColor*vAmbient + finalColor*vDiffuse + finalColor*vSpecular;
}