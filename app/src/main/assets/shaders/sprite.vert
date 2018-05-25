attribute vec4 a_position;
attribute vec2 a_texture_position;
varying vec2 v_texture_position;
uniform mat4 u_matrix;
uniform mat4 u_texture_matrix;
uniform vec2 u_texture_scale;

void main(){
	v_texture_position = (u_texture_matrix * vec4(a_texture_position*u_texture_scale, 0.0, 1.0)).xy;
	//v_texture_position = a_texture_position;
	gl_Position = u_matrix * a_position;
}
