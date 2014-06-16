uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D ShadowMap;

varying vec4 color;
varying vec2 texCoord;

void main() {
  	gl_FragColor = color;
}