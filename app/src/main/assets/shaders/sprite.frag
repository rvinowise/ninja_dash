precision mediump float;
varying vec2 v_texture_position;
uniform sampler2D u_texture_unit;
void main(){
	gl_FragColor= texture2D(u_texture_unit, v_texture_position);
}
