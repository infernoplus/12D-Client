package twelveengine.graphics;

import static org.lwjgl.opengl.GL20.*;
import twelveengine.data.Vertex;

public class GlowLight {
	public Vertex position;
	public Vertex color;
	public float radius;
	
	public GlowLight(Vertex p, Vertex c, float r) {
		position = p;
		color = c;
		radius = r;
	}
	
	public void applyToShader(int n) {
		glUniform3f(glGetUniformLocation(GraphicsCore.activeProgram, "glowLoc[" + n + "]"), position.x, position.y, position.z);
		glUniform4f(glGetUniformLocation(GraphicsCore.activeProgram, "glowColor[" + n + "]"), color.x, color.y, color.z, 1.0f);
		glUniform1f(glGetUniformLocation(GraphicsCore.activeProgram, "glowRadius[" + n + "]"), radius);
	}
}
